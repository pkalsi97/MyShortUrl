package com.pk.MyShortUrl.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.pk.MyShortUrl.config.AppConfig;
import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.repository.ShortURLRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ShortURLService {

    private final ShortURLRepository shortURLRepository;
    private final AppConfig appConfig;


    private static final Logger logger = LoggerFactory.getLogger(ShortURLService.class);
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
    private static final int BACKHALF_LENGTH = 5;
    private static final Random random = new SecureRandom();


    public boolean isBackHalfUnique(String backHalf) {
        return shortURLRepository.findByShortLink(appConfig.getBaseUrl() + backHalf).isEmpty();
    }

    public ShortURL createShortURL(String originalUrl, String backHalf, String userId) {

        String allowedCharsRegex = "^[A-Za-z0-9_-]+$";

        if (!backHalf.matches(allowedCharsRegex)) {
            throw new IllegalArgumentException("The backHalf contains invalid characters.");
        }

        if (isBackHalfUnique(backHalf)) {
            ShortURL shortURL = new ShortURL();
            shortURL.setOriginalUrl(originalUrl);
            String shortLink = appConfig.getBaseUrl() + backHalf;
            shortURL.setShortLink(shortLink);
            shortURL.setUserId(userId);
            shortURL.setActive(true);
            shortURL.setCreationDate(LocalDateTime.now());
            shortURL.setExpirationDate(shortURL.getCreationDate().plusHours(appConfig.getTimeAllotted()));
            shortURL.setQrCode(generateQrCode(shortLink));
            return shortURLRepository.save(shortURL);
        }
        return null;
    }

    private String generateQrCode(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] pngData = pngOutputStream.toByteArray();
            return Base64.getEncoder().encodeToString(pngData);
        } catch (Exception e) {
            logger.error("Error generating QR Code", e);
            return null;
        }
    }

    public List<ShortURL> getAllShortURLsByUser(String userId) {
        return shortURLRepository.findAllByUserId(userId);
    }

    public Optional<ShortURL> getShortURLByShortLink(String shortLink) {
        return shortURLRepository.findByShortLink(shortLink);
    }

    public List<ShortURL> getActiveShortURLsByUser(String userId) {
        return shortURLRepository.findAllByUserIdAndActive(userId, true);
    }

    public boolean deactivateUrl(String urlId, String username) {
        Optional<ShortURL> shortURLOpt = shortURLRepository.findById(urlId);
        if (shortURLOpt.isPresent() && shortURLOpt.get().getUserId().equals(username)) {
            ShortURL shortURL = shortURLOpt.get();
            shortURL.setActive(false);
            shortURLRepository.save(shortURL);
            return true;
        }
        return false;
    }

    public boolean isBackHalfValid(String backHalf) {
        return !appConfig.getReservedPaths().contains(backHalf);
    }

    public boolean isBackHalfAvailable(String backHalf) {
        return shortURLRepository.findByShortLink(appConfig.getBaseUrl() + backHalf).isEmpty();
    }

    public String generateUniqueBackHalf() {
        while (true) {
            String backHalf = generateRandomBackHalf(BACKHALF_LENGTH);
            if (isBackHalfUnique(backHalf)) {
                return backHalf;
            }
        }
    }
    public String generateRandomBackHalf(int length) {
        StringBuilder backHalf = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            backHalf.append(CHAR_POOL.charAt(index));
        }
        return backHalf.toString();
    }

    public List<ShortURL> getInactiveShortURLsByUser(String userId) {
        return shortURLRepository.findAllByUserIdAndActive(userId, false);
    }

    public void incrementClickCount(String shortLinkId) {
        Optional<ShortURL> shortURLOpt = shortURLRepository.findById(shortLinkId);
        if (shortURLOpt.isPresent()) {
            ShortURL shortURL = shortURLOpt.get();
            shortURL.setClickCount(shortURL.getClickCount() + 1);
            shortURLRepository.save(shortURL);
        }
    }

    public Optional<ShortURL> findByShortLink(String shortLink) {
        return shortURLRepository.findByShortLink(shortLink);
    }
}
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ShortURLService {

    private final ShortURLRepository shortURLRepository;
    private final AppConfig appConfig;
    private static final Logger logger = LoggerFactory.getLogger(ShortURLService.class);

    public boolean isBackHalfUnique(String backHalf) {
        return shortURLRepository.findByShortLinkAndActive(appConfig.getBaseUrl() + backHalf, true).isEmpty();
    }

    public ShortURL createShortURL(String originalUrl, String backHalf, String userId) {
        if (isBackHalfUnique(backHalf)) {
            ShortURL shortURL = new ShortURL();
            shortURL.setOriginalUrl(originalUrl);
            String shortLink = appConfig.getBaseUrl() + backHalf;
            shortURL.setShortLink(shortLink);
            shortURL.setUserId(userId);
            shortURL.setActive(true);
            shortURL.setCreationDate(LocalDateTime.now());
            shortURL.setExpirationDate(shortURL.getCreationDate().plusHours(48));
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


    private final Set<String> reservedPaths = Set.of(
            "login", "logout", "register", "dashboard", "active-urls", "error"
    );

    public boolean isBackHalfValid(String backHalf) {
        return !reservedPaths.contains(backHalf);
    }

}

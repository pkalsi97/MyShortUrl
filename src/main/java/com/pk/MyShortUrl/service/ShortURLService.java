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

    // takes care of creation of both custom and random
    public ShortURL createShortURL(String originalUrl, String backHalf, String userId) {
        // use Google webrisk API to  see if Original url is on any of the unsafe list
        boolean isSafe = webriskSearchUri.searchUri(originalUrl);
        if(!isSafe){
            throw new IllegalArgumentException("Original Url is not safe");
        }
        // check if back half contains any invalid characters
        String allowedCharsRegex = "^[A-Za-z0-9_-]+$";
        if (!backHalf.matches(allowedCharsRegex)) {
            throw new IllegalArgumentException("The backHalf contains invalid characters.");
        }

        // Check if the original URL is the same as the short URL
        String shortLinkCheck = appConfig.getBaseUrl() + "/" + backHalf;
        if (originalUrl.equals(shortLinkCheck)) {
            throw new IllegalArgumentException("Original URL cannot be the same as the short URL.");
        }
        // Check if the original URL is a short URL already in the database
        if (shortURLRepository.existsByShortLink(originalUrl)) {
            throw new IllegalArgumentException("The provided URL is already a short URL.");
        }

        // if the back half is unique proceed ->
        if (isBackHalfUnique(backHalf)) {
            ShortURL shortURL = new ShortURL();
            shortURL.setOriginalUrl(originalUrl);
            String shortLink = appConfig.getBaseUrl() +"/"+backHalf;
            shortURL.setShortLink(shortLink);
            shortURL.setUserId(userId);
            shortURL.setActive(true);
            shortURL.setCreationDate(LocalDateTime.now());
            shortURL.setExpirationDate(shortURL.getCreationDate().plusHours(appConfig.getTimeAllotted()));
            shortURL.setQrCode(generateQrCode(originalUrl));
            return shortURLRepository.save(shortURL);
        }
        return null;
    }

    // use google zxing to generate the QR code
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

    // Method to deactivate the url by id
    public boolean deactivateUrl(String urlId, String username) {
        Optional<ShortURL> shortURLOpt = shortURLRepository.findById(urlId);
        if (shortURLOpt.isPresent()) {
            ShortURL shortURL = shortURLOpt.get();
            if (shortURL.getUserId().equals(username) && shortURL.isActive()) {
                shortURL.setActive(false);
                shortURLRepository.save(shortURL);
                return true;
            }
        }
        return false;
    }

    // method to deactivate url by shortlist
    public boolean deactivateUrlByShortLink(String shortLink, String username) {
        Optional<ShortURL> shortURLOpt = shortURLRepository.findByShortLink(shortLink);
        if (shortURLOpt.isPresent() && shortURLOpt.get().getUserId().equals(username)) {
            ShortURL shortURL = shortURLOpt.get();
            if(!shortURL.isActive()) return false;
            shortURL.setActive(false);
            shortURLRepository.save(shortURL);
            return true;
        }
        return false;
    }

    // METHOD TO GENERATE RANDOM UNIQUE BACKHALF
    public String generateUniqueBackHalf() {
        while (true) {
            String backHalf = generateRandomBackHalf(BACKHALF_LENGTH);
            if (isBackHalfUnique(backHalf)) {
                return backHalf;
            }
        }
    }
    //
    public String generateRandomBackHalf(int length) {
        StringBuilder backHalf = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            //get random index
            int index = random.nextInt(CHAR_POOL.length());
            backHalf.append(CHAR_POOL.charAt(index));
        }
        return backHalf.toString();
    }

    // get list of inactive urls
    public List<ShortURL> getInactiveShortURLsByUser(String userId) {
        return shortURLRepository.findAllByUserIdAndActive(userId, false);
    }

    //increment click count of short url
    public void incrementClickCount(String shortLinkId) {
        Optional<ShortURL> shortURLOpt = shortURLRepository.findById(shortLinkId);
        if (shortURLOpt.isPresent()) {
            ShortURL shortURL = shortURLOpt.get();
            shortURL.setClickCount(shortURL.getClickCount() + 1);
            shortURLRepository.save(shortURL);
        }
    }
    //Get all short urls by userid
    public List<ShortURL> getAllShortURLsByUser(String userId) {
        return shortURLRepository.findAllByUserId(userId);
    }

    // get all short url by shortlink
    public Optional<ShortURL> getShortURLByShortLink(String shortLink) {
        return shortURLRepository.findByShortLink(shortLink);
    }
    // get active short links
    public List<ShortURL> getActiveShortURLsByUser(String userId) {
        return shortURLRepository.findAllByUserIdAndActive(userId, true);
    }
    // check if back half is unique
    public boolean isBackHalfUnique(String backHalf) {
        return shortURLRepository.findByShortLink(appConfig.getBaseUrl()+"/"+ backHalf).isEmpty();
    }
    //check if the back half is valid
    public boolean isBackHalfValid(String backHalf) {
        return !appConfig.getReservedPaths().contains(backHalf);
    }
}
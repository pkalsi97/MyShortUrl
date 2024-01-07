package com.pk.MyShortUrl.service;

import com.pk.MyShortUrl.model.ShortURL;
import com.pk.MyShortUrl.repository.ShortURLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlExpirationService {

    private final ShortURLRepository shortURLRepository;

    @Scheduled(fixedRate = 30000)
    public void deactivateExpiredUrls() {
        LocalDateTime nowInIst = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));

        Date currentDateInIst = Date.from(nowInIst.atZone(ZoneId.systemDefault()).toInstant());

        Date startDate = Date.from(nowInIst.minusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(nowInIst.plusMinutes(1).atZone(ZoneId.systemDefault()).toInstant());

        List<ShortURL> urlsToDeactivate = shortURLRepository.findByActiveTrueAndExpirationDateBetween(startDate, endDate);

        for (ShortURL url : urlsToDeactivate) {
            url.setActive(false);
            shortURLRepository.save(url);
        }
    }
}

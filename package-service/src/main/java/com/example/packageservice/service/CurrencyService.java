package com.example.packageservice.service;

import com.example.packageservice.dto.FrankfurterResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CurrencyService implements ICurrencyService {

    private final RestTemplate restTemplate;
    private Map<String, BigDecimal> ratesCache = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    public CurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Load rates on startup
    @PostConstruct
    public void init() {
        refreshCache();
    }

    public BigDecimal convert(BigDecimal usdAmount, String desiredCurrency) {
        if (desiredCurrency.equalsIgnoreCase("USD"))
            return usdAmount;

        BigDecimal rate = ratesCache.get(desiredCurrency.toUpperCase());
        if (rate == null) {
            logger.error("Currency rate {} not found in currency rates cache", desiredCurrency);
            throw new IllegalArgumentException(String.format("A rate for currency %s was not found", desiredCurrency));
        }

        return usdAmount.multiply(rate);
    }

    // Refresh rates cache daily at 16:05 CET (They are updated daily at about 16:00 CET)
    @Scheduled(cron = "0 5 16 * * *", zone = "CET")
    public void refreshCache() {
        try {
            String url = "https://api.frankfurter.app/latest?base=USD";
            FrankfurterResponse response = restTemplate.getForObject(url, FrankfurterResponse.class);

            if (response != null && response.rates() != null) {
                ratesCache = new ConcurrentHashMap<>(response.rates());
                logger.info("Currency rates cache updated with {} items", ratesCache.size());
            }
        } catch (Exception e) {
            logger.error("Failed to refresh currency rates cache", e);
        }
    }
}
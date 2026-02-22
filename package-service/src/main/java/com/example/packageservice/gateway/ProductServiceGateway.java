package com.example.packageservice.gateway;

import com.example.packageservice.gateway.dto.Product;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ProductServiceGateway implements IProductServiceGateway {

    private final RestTemplate restTemplate;
    private Map<String, Product> productsCache = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceGateway.class);

    public ProductServiceGateway(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Load products on startup
    @PostConstruct
    public void init() {
        refreshCache();
    }

    public List<Product> getProductsByIds(List<String> ids) {
        return ids.stream()
                .map(id -> {
                    Product product = productsCache.get(id);

                    if (product == null) {
                        logger.error("Product with id {} not found in products cache", id);
                        throw new IllegalArgumentException(String.format("Product with id %s was not found", id));
                    }

                    return product;
                })
                .toList();
    }

    // Refresh product cache daily at 00:00 GMT
    @Scheduled(cron = "0 0 0 * * *", zone = "GMT")
    public void refreshCache() {
        try {
            String url = "https://product-service.herokuapp.com/api/v1/products";
            Product[] products = restTemplate.getForObject(url, Product[].class);

            if (products != null) {
                Map<String, Product> newCache = new HashMap<>(products.length);
                for (Product p : products)
                    newCache.put(p.id(), p);

                productsCache = Map.copyOf(newCache);
                logger.info("Products cache updated with {} items", productsCache.size());
            }
        }
        catch (Exception e) {
            logger.error("Failed to refresh product cache", e);
        }
    }
}
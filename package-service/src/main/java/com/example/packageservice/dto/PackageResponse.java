package com.example.packageservice.dto;

import com.example.packageservice.gateway.dto.Product;

import java.math.BigDecimal;
import java.util.List;

public record PackageResponse(String id, String name, String description, List<Product> products, BigDecimal totalPrice, String currency) {
}
package com.example.packageservice.gateway.dto;

import java.math.BigDecimal;

public record Product(String id, String name, BigDecimal usdPrice) {
}

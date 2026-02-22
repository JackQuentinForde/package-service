package com.example.packageservice.dto;

import java.math.BigDecimal;
import java.util.Map;

public record FrankfurterResponse(String base, String date, Map<String, BigDecimal> rates) {
}

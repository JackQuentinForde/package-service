package com.example.packageservice.service;

import java.math.BigDecimal;

public interface ICurrencyService {
    BigDecimal convert(BigDecimal usdAmount, String desiredCurrency);
}
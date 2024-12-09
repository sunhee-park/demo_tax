package com.example.demo.util.taxcalculator;

import java.math.BigDecimal;

public interface TaxCalculator {
    BigDecimal calculateTax(BigDecimal taxableIncome);
}

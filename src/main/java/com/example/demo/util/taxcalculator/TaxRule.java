package com.example.demo.util.taxcalculator;

import java.math.BigDecimal;

public interface TaxRule {
    boolean isApplicable(BigDecimal taxableIncome);

    BigDecimal calculate(BigDecimal taxableIncome);
}

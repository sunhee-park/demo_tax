package com.example.demo.util.taxcalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DefaultTaxRule implements TaxRule {
    private final BigDecimal lowerBound;
    private final BigDecimal upperBound;
    private final BigDecimal baseTax;
    private final BigDecimal rate;

    public DefaultTaxRule(BigDecimal lowerBound, BigDecimal upperBound, BigDecimal baseTax, BigDecimal rate) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.baseTax = baseTax;
        this.rate = rate;
    }

    @Override
    public boolean isApplicable(BigDecimal taxableIncome) {
        return taxableIncome.compareTo(lowerBound) > 0 && (upperBound == null || taxableIncome.compareTo(upperBound) <= 0);
    }

    @Override
    public BigDecimal calculate(BigDecimal taxableIncome) {
        BigDecimal excess = taxableIncome.subtract(lowerBound).setScale(0, RoundingMode.HALF_UP);
        return baseTax.add(excess.multiply(rate).setScale(0, RoundingMode.HALF_UP));
    }
}

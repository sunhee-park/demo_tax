package com.example.demo.util.taxcalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DefaultTaxCalculator implements TaxCalculator {
    private final List<TaxRule> rules;

    public DefaultTaxCalculator() {
        this.rules = new ArrayList<>();

        // 기본 세율 규칙 정의
        rules.add(new DefaultTaxRule(BigDecimal.ZERO, new BigDecimal("14000000"), BigDecimal.ZERO, new BigDecimal("0.06")));
        rules.add(new DefaultTaxRule(new BigDecimal("14000000"), new BigDecimal("50000000"), new BigDecimal("840000"), new BigDecimal("0.15")));
        rules.add(new DefaultTaxRule(new BigDecimal("50000000"), new BigDecimal("88000000"), new BigDecimal("6240000"), new BigDecimal("0.24")));
        rules.add(new DefaultTaxRule(new BigDecimal("88000000"), new BigDecimal("150000000"), new BigDecimal("15360000"), new BigDecimal("0.35")));
        rules.add(new DefaultTaxRule(new BigDecimal("150000000"), new BigDecimal("300000000"), new BigDecimal("37060000"), new BigDecimal("0.38")));
        rules.add(new DefaultTaxRule(new BigDecimal("300000000"), new BigDecimal("500000000"), new BigDecimal("94060000"), new BigDecimal("0.40")));
        rules.add(new DefaultTaxRule(new BigDecimal("500000000"), new BigDecimal("1000000000"), new BigDecimal("174060000"), new BigDecimal("0.42")));
        rules.add(new DefaultTaxRule(new BigDecimal("1000000000"), null, new BigDecimal("384060000"), new BigDecimal("0.45")));
    }

    @Override
    public BigDecimal calculateTax(BigDecimal taxableIncome) {
        for (TaxRule rule : rules) {
            if (rule.isApplicable(taxableIncome)) {
                return rule.calculate(taxableIncome);
            }
        }
        throw new IllegalArgumentException("적용 가능한 세율 규칙이 없습니다.");
    }
}

package com.example.demo.util.taxcalculator;

public class TaxCalculatorFactory {

    public static TaxCalculator createCalculator(String calculationMethod) {
        if ("custom".equalsIgnoreCase(calculationMethod)) {
            return null;
        } else {
            return new DefaultTaxCalculator();
        }
    }
}

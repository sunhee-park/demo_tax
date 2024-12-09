package com.example.demo.service;

import com.example.demo.entity.IncomeDetails;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.repository.IncomeDetailsRepository;
import com.example.demo.util.taxcalculator.TaxCalculator;
import com.example.demo.util.taxcalculator.TaxCalculatorFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class IncomeTaxCalculationService {
    private final IncomeDetailsRepository incomeDetailsRepository;

    public IncomeTaxCalculationService(IncomeDetailsRepository incomeDetailsRepository) {
        this.incomeDetailsRepository = incomeDetailsRepository;
    }

    public String calculateRefund(Long id) {

        // 사용자 소득 정보 조회
        IncomeDetails incomeDetails = incomeDetailsRepository.findByUserId(id)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        BigDecimal taxableIncome = incomeDetails.getTotalIncome().subtract(incomeDetails.getDeductions());

        // Factory에서 적절한 계산기 생성
        TaxCalculator calculator = TaxCalculatorFactory.createCalculator("default");

        BigDecimal calculatedTax = calculator.calculateTax(taxableIncome);
        BigDecimal finalTax = calculatedTax.subtract(incomeDetails.getTaxCredit());

        // 천 단위 구분 적용
        return NumberFormat.getInstance(Locale.KOREA).format(finalTax);
    }
}
package com.example.demo.controller;

import com.example.demo.dto.ScrapRequest;
import com.example.demo.entity.IncomeDetails;
import com.example.demo.security.CustomUserDetails;
import com.example.demo.service.IncomeScrapingService;
import com.example.demo.service.IncomeTaxCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/szs")
public class IncomeController {
    private final IncomeScrapingService incomeScrapingService;
    private final IncomeTaxCalculationService incomeTaxCalculationService;

    public IncomeController(IncomeScrapingService incomeScrapingService, IncomeTaxCalculationService incomeTaxCalculationService) {
        this.incomeScrapingService = incomeScrapingService;
        this.incomeTaxCalculationService = incomeTaxCalculationService;
    }

    @PostMapping("/scrap")
    public ResponseEntity<?> scrap(@RequestBody ScrapRequest request) throws Exception {
        IncomeDetails incomeDetails = incomeScrapingService.getIncomeDetailsFromScrap(request.getName(), request.getRegNo());
        // 사용자 정보를 제외한 응답 생성
        return ResponseEntity.ok().body(Map.of(
                "totalIncome", incomeDetails.getTotalIncome(),
                "deductions", incomeDetails.getDeductions(),
                "taxCredit", incomeDetails.getTaxCredit()
        ));
    }

    @GetMapping("/refund")
    public ResponseEntity<?> refund(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String refundAmount = incomeTaxCalculationService.calculateRefund(userDetails.getUsers().getId());
        return ResponseEntity.ok().body(Map.of("결정세액", refundAmount));
    }
}

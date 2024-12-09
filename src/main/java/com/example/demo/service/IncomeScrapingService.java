package com.example.demo.service;

import com.example.demo.entity.IncomeDetails;
import com.example.demo.entity.Users;
import com.example.demo.exception.CustomException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.integration.ScrapClient;
import com.example.demo.integration.incomescraping.IncomeResponse;
import com.example.demo.repository.IncomeDetailsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.util.RrnEncryptionUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class IncomeScrapingService {
    private final ScrapClient scrapClient;
    private final IncomeDetailsRepository incomeDetailsRepository;
    private final UsersRepository usersRepository;
    private final RrnEncryptionUtil rrnEncryptionUtil;

    public IncomeScrapingService(ScrapClient scrapClient, IncomeDetailsRepository incomeDetailsRepository, UsersRepository usersRepository, RrnEncryptionUtil rrnEncryptionUtil) {
        this.scrapClient = scrapClient;
        this.incomeDetailsRepository = incomeDetailsRepository;
        this.usersRepository = usersRepository;
        this.rrnEncryptionUtil = rrnEncryptionUtil;
    }

    @Transactional
    public IncomeDetails getIncomeDetailsFromScrap(String name, String regNo) throws Exception {
        // 사용자 조회
        Users user = usersRepository.findByNameAndRegNo(name, rrnEncryptionUtil.encrypt(regNo))
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 스크래핑 데이터 가져오기
        IncomeResponse incomeResponse = scrapClient.fetchScrapData(name, regNo);

        // IncomeResponse 소득정보 가져오기
        BigDecimal totalIncome = incomeResponse.getData().getTotalIncome(); // 종합소득금액
        BigDecimal totalDeductions = incomeResponse.getData().getDeductions().calculateTotalDeductions(); // 소득공제 합산
        BigDecimal taxCredit = incomeResponse.getData().getDeductions().getTaxCredit(); // 세액공제

        // 데이터베이스에서 기존 데이터 조회
        IncomeDetails incomeDetails = incomeDetailsRepository.findByUser(user)
                .orElse(new IncomeDetails()); // 없으면 새 객체 생성

        // 기존 데이터 업데이트 또는 새 데이터 설정
        incomeDetails.setUser(user);
        incomeDetails.setTotalIncome(totalIncome);
        incomeDetails.setDeductions(totalDeductions);
        incomeDetails.setTaxCredit(taxCredit);
        incomeDetails.setUpdatedAt(LocalDateTime.now());

        return incomeDetailsRepository.save(incomeDetails); // 업데이트 또는 삽입
    }
}

package com.example.demo.integration.incomescraping;

import com.example.demo.util.CustomBigDecimalDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class Deductions {
    @JsonProperty("국민연금")
    private List<PensionDeduction> nationalPension;

    @JsonProperty("신용카드소득공제")
    private CreditCardDeduction creditCardDeduction;

    @JsonDeserialize(using = CustomBigDecimalDeserializer.class)
    @JsonProperty("세액공제")
    private BigDecimal taxCredit;

    /**
     * 총 소득공제 계산 메서드
     */
    public BigDecimal calculateTotalDeductions() {
        // 국민연금 공제액 합산
        BigDecimal totalNationalPension = nationalPension.stream()
                .map(PensionDeduction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 신용카드 공제액 합산
        BigDecimal totalCreditCard = creditCardDeduction.getMonth().stream()
                .flatMap(map -> map.values().stream()) // Map의 값들을 스트림으로 변환
                .map(value -> new BigDecimal(value.replace(",", ""))) // 천 단위 구분 제거 후 BigDecimal로 변환
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 합산

        // 총합 반환
        return totalNationalPension.add(totalCreditCard);
    }
}

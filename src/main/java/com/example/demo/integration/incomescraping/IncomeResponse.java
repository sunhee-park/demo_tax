package com.example.demo.integration.incomescraping;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class IncomeResponse {
    private String status;                 // "success"
    private Data data;                     // 스크래핑 응답 데이터
    private Errors errors;                 // 오류 정보
}

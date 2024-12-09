package com.example.demo.integration.incomescraping;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class Data {
    @JsonProperty("이름")
    private String name;

    @JsonProperty("종합소득금액")
    private BigDecimal totalIncome;

    @JsonProperty("소득공제")
    private Deductions deductions;

}

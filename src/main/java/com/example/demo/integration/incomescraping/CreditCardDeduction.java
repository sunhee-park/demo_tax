package com.example.demo.integration.incomescraping;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class CreditCardDeduction {
    @JsonProperty("year")
    private int year;

    @JsonProperty("month")
    private List<Map<String, String>> month;

}

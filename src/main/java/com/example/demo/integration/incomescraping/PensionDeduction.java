package com.example.demo.integration.incomescraping;

import com.example.demo.util.CustomBigDecimalDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class PensionDeduction {
    @JsonProperty("월")
    private String month;

    @JsonDeserialize(using = CustomBigDecimalDeserializer.class)
    @JsonProperty("공제액")
    private BigDecimal amount;
}

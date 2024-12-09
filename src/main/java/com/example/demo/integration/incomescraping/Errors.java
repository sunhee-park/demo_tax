package com.example.demo.integration.incomescraping;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Errors {
    private String code;         // "code"
    private String message;      // "message"
    private String validations;  // "validations"
}

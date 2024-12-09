package com.example.demo.integration;

import com.example.demo.config.ScrapProperties;
import com.example.demo.integration.incomescraping.IncomeResponse;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ScrapClient {
    private final ExternalHttpClient externalHttpClient;
    private final ScrapProperties scrapProperties;

    public ScrapClient(ExternalHttpClient externalHttpClient, ScrapProperties scrapProperties) {
        this.externalHttpClient = externalHttpClient;
        this.scrapProperties = scrapProperties;
    }

    /**
     * 스크래핑 데이터를 가져오는 메서드
     */
    public IncomeResponse fetchScrapData(String name, String regNo) {
        // 요청 헤더
        Map<String, String> headers = Map.of(
                "X-API-Key", scrapProperties.getApiKey()
        );

        // 요청 Body
        Map<String, String> body = Map.of(
                "name", name,
                "regNo", regNo
        );

        // HTTP POST 요청
        return externalHttpClient.post(scrapProperties.getEndPoint(), headers, body, IncomeResponse.class);
    }
}

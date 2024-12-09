package com.example.demo.integration;

import com.example.demo.config.ScrapProperties;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.util.Map;

@Slf4j
@Component
public class ExternalHttpClient {

    private final WebClient webClient;

    public ExternalHttpClient(WebClient.Builder webClientBuilder, ScrapProperties scrapProperties) {
        // 커넥션 풀 설정
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(200) // 최대 커넥션 수
                .pendingAcquireMaxCount(1000) // 대기 가능한 최대 요청 수
                .build();

        // HttpClient 설정
        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000) // 연결 타임아웃
                .responseTimeout(java.time.Duration.ofSeconds(15));   // 응답 타임아웃

        // WebClient 설정
        this.webClient = webClientBuilder
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(scrapProperties.getBaseUrl()) // 기본 URL 설정 (필요 시 수정 가능)
                .filter(logRequest())  // 요청 로깅 필터 추가
                .filter(logResponse()) // 응답 로깅 필터 추가
                .build();
    }

    /**
     * HTTP POST 요청
     * @param endPoint 요청 URL endPoint
     * @param headers 요청 헤더
     * @param body 요청 Body
     * @return 응답 데이터를 responseType 으로 반환
     */
    public <T> T post(String endPoint, Map<String, String> headers, Map<String, String> body, Class<T> responseType) {
        try {

            WebClient.RequestHeadersSpec<?> request = webClient.post()
                    .uri(endPoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body);

            if (headers != null) {
                headers.forEach(request::header);
            }

            return request.retrieve()
                    .bodyToMono(responseType) // 응답을 특정 객체 타입으로 변환
                    .block();

        } catch (WebClientResponseException e) {
            throw new RuntimeException(
                    String.format("HTTP 요청 실패: %s - %s", e.getStatusCode(), e.getResponseBodyAsString()), e);
        } catch (Exception e) {
            throw new RuntimeException("HTTP 요청 처리 중 예외 발생: " + e.getMessage(), e);
        }
    }

    // 요청 정보 로깅 필터
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            log.info("HTTP 요청 - 메서드: {}, URL: {}", clientRequest.method(), clientRequest.url());
            log.info("HTTP 요청 - 요청 헤더: {}", clientRequest.headers());
            return Mono.just(clientRequest);
        });
    }

    // 응답 정보 로깅 필터
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            log.info("HTTP 응답 - 상태코드: {}", clientResponse.statusCode());
            return Mono.just(clientResponse);
        });
    }

}

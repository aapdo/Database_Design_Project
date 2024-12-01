package com.drawit.drawit.service;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class HttpRequestService {

    private final RestTemplate restTemplate;

    /**
     * GET 요청 보내기
     *
     * @param url 요청을 보낼 URL
     * @return 서버에서 받은 응답
     */
    public String sendGetRequest(String url) {
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * POST 요청 보내기
     *
     * @param url 요청을 보낼 URL
     * @param body 요청에 포함할 데이터 (JSON 등)
     * @return 서버에서 받은 응답
     */
    public String sendPostRequest(String url, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json"); // JSON 형식 요청 설정

        HttpEntity<String> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        return response.getBody(); // 응답 바디 리턴
    }
}
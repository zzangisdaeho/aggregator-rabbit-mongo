package com.example.aggregatormongo.outbound.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpOutbound{

    private final RestTemplate restTemplate = new RestTemplate();


    public void dispatch(HttpMethod httpMethod, String httpAddress, Object payload) {
        ResponseEntity<Object> exchange = restTemplate.exchange(httpAddress, httpMethod, new HttpEntity<>(payload), Object.class);
        if(exchange.getStatusCode() != HttpStatus.OK) log.error("200아닌데욥");
    }
}

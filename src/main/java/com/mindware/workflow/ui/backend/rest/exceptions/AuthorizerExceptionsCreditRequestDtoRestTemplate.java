package com.mindware.workflow.ui.backend.rest.exceptions;

import com.mindware.workflow.ui.backend.entity.exceptions.AuthorizerExceptionsCreditRequestDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class AuthorizerExceptionsCreditRequestDtoRestTemplate {
    private static RestTemplate restTemplate;

    public AuthorizerExceptionsCreditRequestDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<AuthorizerExceptionsCreditRequestDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getAll";
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizerExceptionsCreditRequestDto> getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getByCity";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizerExceptionsCreditRequestDto> getByCityCurrencyAmounts(String city, String currency, Double minimum, Double maximum){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getByCityCurrencyAmounts";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);
        headers.set("currency", currency);
        headers.set("minimum", minimum.toString());
        headers.set("maximum", maximum.toString());
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizerExceptionsCreditRequestDto> getByCurrencyAmounts(String currency, Double minimum, Double maximum){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getByCurrencyAmounts";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("currency", currency);
        headers.set("minimum", minimum.toString());
        headers.set("maximum", maximum.toString());
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizerExceptionsCreditRequestDto> getByUser(String loginUser){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getByUser";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("login-user",loginUser);
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizerExceptionsCreditRequestDto> getByRiskType(String riskType){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getByRiskType";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("risk-type",riskType);
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizerExceptionsCreditRequestDto> getByRiskTypeCity(String riskType, String city){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getByRiskTypeCity";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("risk-type",riskType);
        headers.set("city",city);
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

}

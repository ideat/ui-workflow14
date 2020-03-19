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

    public List<AuthorizerExceptionsCreditRequestDto> getByUser(String loginUser){
        final String uri = "http://localhost:8080/rest/v1/authorizerExceptionCreditRequest/getByUser";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("login-user",loginUser);
        HttpEntity<AuthorizerExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizerExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET
                ,entity,AuthorizerExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }
}

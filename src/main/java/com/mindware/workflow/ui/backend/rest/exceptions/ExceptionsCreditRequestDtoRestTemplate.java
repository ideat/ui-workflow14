package com.mindware.workflow.ui.backend.rest.exceptions;

import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequestDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class ExceptionsCreditRequestDtoRestTemplate {
    private static RestTemplate restTemplate;

    public ExceptionsCreditRequestDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<ExceptionsCreditRequestDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequestDto/getAll";
        HttpEntity<ExceptionsCreditRequestDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<ExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,ExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<ExceptionsCreditRequestDto> getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequestDto/getByNumberRequest";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest.toString());
        HttpEntity<ExceptionsCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<ExceptionsCreditRequestDto[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,ExceptionsCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public ExceptionsCreditRequestDto getByCodeExceptionNumberRequest(String codeException, Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequestDto/getByCodeExceptionNumberRequest";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest.toString());
        headers.set("code-exception",codeException);
        HttpEntity<ExceptionsCreditRequestDto> entity = new HttpEntity<>(headers);
        ResponseEntity<ExceptionsCreditRequestDto> response = restTemplate.exchange(uri,HttpMethod.GET,entity,ExceptionsCreditRequestDto.class);
        return response.getBody();
    }
}

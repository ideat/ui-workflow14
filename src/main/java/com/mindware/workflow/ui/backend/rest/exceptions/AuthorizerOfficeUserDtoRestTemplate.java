package com.mindware.workflow.ui.backend.rest.exceptions;

import com.mindware.workflow.ui.backend.entity.exceptions.AuthorizersOfficeUserDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class AuthorizerOfficeUserDtoRestTemplate {
    private RestTemplate restTemplate;

    public AuthorizerOfficeUserDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<AuthorizersOfficeUserDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/authorizerOfficeUser/getAll";
        HttpEntity<AuthorizersOfficeUserDto> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<AuthorizersOfficeUserDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,AuthorizersOfficeUserDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizersOfficeUserDto> getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/authorizerOfficeUser/getByCity";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);
        HttpEntity<AuthorizersOfficeUserDto> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizersOfficeUserDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,AuthorizersOfficeUserDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizersOfficeUserDto> getByAmountBs(Double minimumAmount, Double maximumAmount){
        final String uri = "http://localhost:8080/rest/v1/authorizerOfficeUser/getByAmountBs";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("maximum-amount-bs",maximumAmount.toString());
        headers.set("minimum-amount-bs", minimumAmount.toString());
        HttpEntity<AuthorizersOfficeUserDto> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizersOfficeUserDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,AuthorizersOfficeUserDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<AuthorizersOfficeUserDto> getByAmountSus(Double minimumAmount, Double maximumAmount){
        final String uri = "http://localhost:8080/rest/v1/authorizerOfficeUser/getByAmountSus";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("maximum-amount-sus",maximumAmount.toString());
        headers.set("minimum-amount-sus", minimumAmount.toString());
        HttpEntity<AuthorizersOfficeUserDto> entity = new HttpEntity<>(headers);
        ResponseEntity<AuthorizersOfficeUserDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,AuthorizersOfficeUserDto[].class);
        return Arrays.asList(response.getBody());
    }
}

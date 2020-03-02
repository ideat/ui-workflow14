package com.mindware.workflow.ui.backend.rest.cashFlow;

import com.mindware.workflow.ui.backend.entity.cashFlow.CashFlowCreditRequestApplicantDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashFlowCreditRequestApplicantDtoRestTemplate {
    private static RestTemplate restTemplate;

    public CashFlowCreditRequestApplicantDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<CashFlowCreditRequestApplicantDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/cashFlowCreditRequestApplicant/getAll";
        HttpEntity<CashFlowCreditRequestApplicantDto> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CashFlowCreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CashFlowCreditRequestApplicantDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<CashFlowCreditRequestApplicantDto> getByLogin(String login){
        final String uri = "http://localhost:8080/rest/v1/cashFlowCreditRequestApplicant/getByLogin/{login}";
        Map<String,String> params = new HashMap<>();
        params.put("login",login);
        HttpEntity<CashFlowCreditRequestApplicantDto> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CashFlowCreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CashFlowCreditRequestApplicantDto[].class,params);
        return Arrays.asList(response.getBody());
    }

    public List<CashFlowCreditRequestApplicantDto> getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/cashFlowCreditRequestApplicant/getByCity/{city}";
        Map<String,String> params = new HashMap<>();
        params.put("city",city);
        HttpEntity<CashFlowCreditRequestApplicantDto> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CashFlowCreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CashFlowCreditRequestApplicantDto[].class,params);
        return Arrays.asList(response.getBody());
    }
}

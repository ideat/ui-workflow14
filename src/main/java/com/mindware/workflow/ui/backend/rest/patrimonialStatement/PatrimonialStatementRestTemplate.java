package com.mindware.workflow.ui.backend.rest.patrimonialStatement;

import com.mindware.workflow.ui.backend.entity.patrimonialStatement.PatrimonialStatement;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class PatrimonialStatementRestTemplate {
    private static RestTemplate restTemplate;

    public PatrimonialStatementRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public PatrimonialStatement add(PatrimonialStatement patrimonialStatement){
        final String uri = "http://localhost:8080/rest/v1/patrimonialstatement/add";
        HttpEntity<PatrimonialStatement> entity = new HttpEntity<>(patrimonialStatement, HeaderJwt.getHeader());
        ResponseEntity<PatrimonialStatement> response = restTemplate.postForEntity(uri,entity,PatrimonialStatement.class);
        return response.getBody();
    }

    public List<PatrimonialStatement> getByIdCreditRequestApplicantCategory(UUID idCreditRequestApplicant, String category){
        final String uri = "http://localhost:8080/rest/v1/patrimonialstatement/getByIdCreditRequestApplicantCategory";

        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("idCreditRequestApplicant", idCreditRequestApplicant.toString());
        headers.set("category",category);
        HttpEntity<List<PatrimonialStatement>> entity = new HttpEntity<>(headers);

        ResponseEntity<PatrimonialStatement[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,PatrimonialStatement[].class);
        return Arrays.asList(response.getBody());
    }

    public List<PatrimonialStatement> getByIdCreditRequestApplicantCategoryElement(UUID idCreditRequestApplicant, String category, String element){
        final String uri = "http://localhost:8080/rest/v1/patrimonialstatement/getByIdCreditRequestApplicantCategoryElement";

        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("idCreditRequestApplicant", idCreditRequestApplicant.toString());
        headers.set("category",category);
        headers.set("element", element);
        HttpEntity<List<PatrimonialStatement>> entity = new HttpEntity<>(headers);

        ResponseEntity<PatrimonialStatement[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,PatrimonialStatement[].class);
        return Arrays.asList(response.getBody());
    }

    public void delete(String id){
        final String uri = "http://localhost:8080/rest/v1/patrimonialstatement/delete/{id}";
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<?> request = new HttpEntity<Object>(HeaderJwt.getHeader());
        restTemplate.exchange(uri, HttpMethod.DELETE,request,Void.class,params);
    }
}

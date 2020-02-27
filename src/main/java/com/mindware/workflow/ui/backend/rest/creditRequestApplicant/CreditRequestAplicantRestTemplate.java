package com.mindware.workflow.ui.backend.rest.creditRequestApplicant;

import com.mindware.workflow.ui.backend.entity.CreditRequestApplicant;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


public class CreditRequestAplicantRestTemplate {
    private static RestTemplate restTemplate;

    public CreditRequestAplicantRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public CreditRequestApplicant add(CreditRequestApplicant creditRequestApplicant){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/add";
        HttpEntity<CreditRequestApplicant> entity = new HttpEntity<>(creditRequestApplicant, HeaderJwt.getHeader());
        ResponseEntity<CreditRequestApplicant> response = restTemplate.postForEntity(uri,entity,CreditRequestApplicant.class);
        return response.getBody();
    }

    public CreditRequestApplicant getCreditRequestApplicant(Integer numberRequest, Integer numberApplicant, String typeRelation){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/getApplicant";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("numberRequest",numberRequest.toString());
        headers.set("numberApplicant",numberApplicant.toString());
        headers.set("typeRelation",typeRelation);
        HttpEntity<CreditRequestApplicant> entity = new HttpEntity<>(headers);

        ResponseEntity<CreditRequestApplicant> response = restTemplate.exchange(uri, HttpMethod.GET, entity, CreditRequestApplicant.class);
        return response.getBody();
    }

    public void delete(String id){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/delete/{id}";
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<?> request = new HttpEntity<Object>(HeaderJwt.getHeader());
        restTemplate.exchange(uri, HttpMethod.DELETE,request,Void.class,params);
    }

    public void update(CreditRequestApplicant creditRequestApplicant){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/update";
        HttpEntity<CreditRequestApplicant> entity = new HttpEntity<>(creditRequestApplicant,HeaderJwt.getHeader());

        restTemplate.put(uri, entity);

    }

}

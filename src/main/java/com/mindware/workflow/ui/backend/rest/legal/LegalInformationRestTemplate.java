package com.mindware.workflow.ui.backend.rest.legal;

import com.mindware.workflow.ui.backend.entity.legal.LegalInformation;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class LegalInformationRestTemplate {
    private static RestTemplate restTemplate;

    public LegalInformationRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public LegalInformation add(LegalInformation legalInformation){
        final String uri = "http://localhost:8080/rest/v1/legalInformation/add";
        HttpEntity<LegalInformation> entity = new HttpEntity<>(legalInformation, HeaderJwt.getHeader());
        ResponseEntity<LegalInformation> response = restTemplate.postForEntity(uri,entity,LegalInformation.class);
        return response.getBody();
    }

    public LegalInformation getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/legalInformation/getByNumberRequest/{number-request}";
        Map<String,Integer> params = new HashMap<>();
        params.put("number-request",numberRequest);
        HttpEntity<LegalInformation> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<LegalInformation> response = restTemplate.exchange(uri, HttpMethod.GET,entity,LegalInformation.class,params);

        return response.getBody();
    }

    public LegalInformation getById(UUID id){
        final String uri = "http://localhost:8080/rest/v1/legalInformation/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<LegalInformation> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<LegalInformation> response = restTemplate.exchange(uri, HttpMethod.GET,entity,LegalInformation.class,params);

        return response.getBody();
    }
}

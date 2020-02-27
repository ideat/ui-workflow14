package com.mindware.workflow.ui.backend.rest.creditRequest;

import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CreditRequestRestTemplate {
    private static RestTemplate restTemplate;
    private List<CreditRequest> creditRequestList = new ArrayList<>();

    public CreditRequestRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<CreditRequest> getAll(){
        final String uri = "http://localhost:8080/rest/v1/creditrequest/getAll";
        HttpEntity<CreditRequest[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditRequest[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequest[].class);
        CreditRequest[] creditRequests = response.getBody(); //restTemplate.getForObject(uri,CreditRequest[].class);
        creditRequestList = Arrays.asList(creditRequests);
        return creditRequestList;
    }

    public CreditRequest getCreditRequestById(UUID id){
        final String uri = "http://localhost:8080/rest/v1/creditrequest/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<CreditRequest> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditRequest> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequest.class,params);

        return response.getBody(); //restTemplate.getForObject(uri,CreditRequest.class,params);
    }

    public CreditRequest addCreditRequest(CreditRequest creditRequest){
        final String uri = "http://localhost:8080/rest/v1/creditrequest/add";

        HttpEntity<CreditRequest> entity = new HttpEntity<>(creditRequest,HeaderJwt.getHeader());
        ResponseEntity<CreditRequest> response = restTemplate.postForEntity(uri,entity,CreditRequest.class);
        return response.getBody();
    }

}

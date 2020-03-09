package com.mindware.workflow.ui.backend.rest.paymentPlan;

import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.entity.creditRequest.PaymentPlan;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class PaymentPlanRestTemplate {
    private static RestTemplate restTemplate;

    public PaymentPlanRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<PaymentPlan> add(CreditRequest creditRequest){
        final String uri = "http://localhost:8080/rest/v1/paymentplan/add";
        HttpEntity<CreditRequest> entity = new HttpEntity<>(creditRequest, HeaderJwt.getHeader());
        ResponseEntity<PaymentPlan[]> response = restTemplate.postForEntity(uri,entity,PaymentPlan[].class);
        return Arrays.asList(response.getBody());
    }

    public List<PaymentPlan> getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/paymentPlan/getByNumberRequest/{numberrequest}";
        Map<String,Integer> params = new HashMap<>();
        params.put("numberrequest",numberRequest);
        HttpEntity<PaymentPlan[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<PaymentPlan[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,PaymentPlan[].class,params);
        return Arrays.asList(response.getBody());

    }

    public List<PaymentPlan> getByIdCreditRequestApplicant(UUID idCreditRequestApplicant){
        final String uri = "http://localhost:8080/rest/v1/paymentplan/getByNumberRequestApplicant";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("idCreditRequestApplicant", idCreditRequestApplicant.toString());

        HttpEntity<PaymentPlan[]> entity = new HttpEntity<>(headers);
        ResponseEntity<PaymentPlan[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,PaymentPlan[].class);
        return Arrays.asList(response.getBody());

    }
}

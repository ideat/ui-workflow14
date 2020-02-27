package com.mindware.workflow.ui.backend.rest.paymentPlan;

import com.mindware.workflow.ui.backend.entity.creditRequest.CreditRequest;
import com.mindware.workflow.ui.backend.entity.creditRequest.PaymentPlan;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

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
}

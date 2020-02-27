package com.mindware.workflow.ui.backend.rest.paymentPlan;

import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class PaymentPlanDtoRestTemplate {
    private static RestTemplate restTemplate;

    public PaymentPlanDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public byte[] report(int numberRequest){
        final String uri = "http://localhost:8080/rest/v1/paymentplanreport/{numberrequest}";
        Map<String,Integer> params = new HashMap<>();
        params.put("numberrequest",numberRequest);
        HttpEntity<byte[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class,params);

        return response.getBody();

    }

}

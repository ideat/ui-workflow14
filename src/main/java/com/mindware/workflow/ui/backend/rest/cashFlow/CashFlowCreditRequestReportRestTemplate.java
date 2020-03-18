package com.mindware.workflow.ui.backend.rest.cashFlow;

import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class CashFlowCreditRequestReportRestTemplate {
    private RestTemplate restTemplate;

    public CashFlowCreditRequestReportRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public byte[] report(String numberRequest){
        final String uri = "http://localhost:8080/rest/v1/cashFlowReport";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> reponse = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return reponse.getBody();
    }
}

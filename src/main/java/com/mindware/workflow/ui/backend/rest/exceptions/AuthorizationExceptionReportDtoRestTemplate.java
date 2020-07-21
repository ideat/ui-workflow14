package com.mindware.workflow.ui.backend.rest.exceptions;

import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class AuthorizationExceptionReportDtoRestTemplate {
    private static RestTemplate restTemplate = new RestTemplate();

    public byte[] report(String numberRequest, String typeException){
        final String uri = "http://localhost:8080/rest//v1/authorizationExceptionReport/getByNumberRequest";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest);
        headers.set("type-exception",typeException);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }
}

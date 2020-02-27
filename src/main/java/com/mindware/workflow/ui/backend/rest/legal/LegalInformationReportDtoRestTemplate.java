package com.mindware.workflow.ui.backend.rest.legal;

import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LegalInformationReportDtoRestTemplate {
    private static RestTemplate restTemplate;

    public LegalInformationReportDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public byte[] report(String numberRequest, String createdBy, String numberApplicant){
        final String uri = "http://localhost:8080/rest/v1/legalInformationReport";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest);
        headers.set("created-by",createdBy);
        headers.set("number-applicant",numberApplicant);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }
}

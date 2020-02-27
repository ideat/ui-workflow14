package com.mindware.workflow.ui.backend.rest.applicantStatement;

import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicantStatementRestTemplate {
    private static RestTemplate restTemplate;

    public ApplicantStatementRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public byte[] reportApplicantStatement(String numberRequest, String idApplicant, String idCreditRequestApplicant, String origin){
        final String uri = "http://localhost:8080/rest/v1/sworeStatementReport";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-credit-request-applicant", idCreditRequestApplicant);
        headers.set("id-applicant",idApplicant);
        headers.set("number-request",numberRequest);
        headers.set("origin-report",origin);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

    public void reportHomeVerification(String numberRequest, String idApplicant, String idCreditRequestApplicant){
        final String uri = "http://localhost:8080/rest/v1/homeVerification";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-credit-request-applicant", idCreditRequestApplicant);
        headers.set("id-applicant",idApplicant);
        headers.set("number-request",numberRequest);

        Path paths = Paths.get(System.getProperties().get("user.home").toString());
        String path = paths.toString() + "/home_verification/" + "homeVerification_"+numberRequest+".xls";

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        try {
            Files.write(Paths.get(path),response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return response.getBody();
    }
}

package com.mindware.workflow.ui.backend.rest.creditResolution;

import com.mindware.workflow.ui.backend.entity.creditResolution.CreditResolution;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class CreditResolutionRestTemplate {
    private static RestTemplate restTemplate;

    public CreditResolutionRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public CreditResolution add(CreditResolution creditResolution){
        final String uri = "http://localhost:8080/rest/v1/creditresolution/add";
        HttpEntity<CreditResolution> entity = new HttpEntity<>(creditResolution, HeaderJwt.getHeader());
        ResponseEntity<CreditResolution> response = restTemplate.postForEntity(uri,entity,CreditResolution.class);
        return response.getBody();
    }

    public CreditResolution getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/creditresolution/getByNumberRequest/{number-request}";
        Map<String,Integer> params = new HashMap<>();
        params.put("number-request",numberRequest);
        HttpEntity<CreditResolution> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditResolution> response = restTemplate.exchange(uri, HttpMethod.GET, entity,CreditResolution.class, params);
        return response.getBody();
    }

    public byte[] reportCreditResolution(String numberRequest, String numberApplicant, String idCreditRequestApplicant){
        final String uri = "http://localhost:8080/rest/v1/creditResolutionReport";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("id-credit-request-applicant",idCreditRequestApplicant);
        headers.set("number-request",numberRequest);
        headers.set("number-applicant",numberApplicant);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

}

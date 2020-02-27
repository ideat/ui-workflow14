package com.mindware.workflow.ui.backend.rest.office;

import com.mindware.workflow.ui.backend.entity.Office;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OfficeRestTemplate {

    private static RestTemplate restTemplate;
    private List<Office> officeList = new ArrayList<>();

    public OfficeRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<Office> getAllOffice(){
        final String uri = "http://localhost:8080/rest/v1/office/getAll";

        HttpEntity<String> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Office[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Office[].class);
        return Arrays.asList(response.getBody());

    }

    public Office addOffice(Office office){
        final String uri = "http://localhost:8080/rest/v1/office/add";

        HttpEntity<Office> entity = new HttpEntity<>(office,HeaderJwt.getHeader());
        ResponseEntity<Office> response = restTemplate.postForEntity(uri, entity,Office.class);
        return response.getBody();

    }
    public void updateOfficeSignature(Office office){
        final String uri = "http://localhost:8080/rest/v1/office/updateSignatorie";
        HttpEntity<Office> entity = new HttpEntity<>(office, HeaderJwt.getHeader());
        restTemplate.exchange(uri, HttpMethod.PUT,entity,Office.class);


    }


}

package com.mindware.workflow.ui.backend.rest.legal;

import com.mindware.workflow.ui.backend.entity.legal.dto.LegalInformationCreditRequestDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class LegalInformationCreditRequestDtoRestTemplate {
    private static RestTemplate restTemplate;

    public LegalInformationCreditRequestDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<LegalInformationCreditRequestDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/legalInformationCreditRequest/getAll";
        HttpEntity<LegalInformationCreditRequestDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<LegalInformationCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,LegalInformationCreditRequestDto[].class);
        LegalInformationCreditRequestDto[] legalInformationCreditRequestDtos = response.getBody();
        return Arrays.asList(legalInformationCreditRequestDtos);
    }
}

package com.mindware.workflow.ui.backend.rest.creditResolution;

import com.mindware.workflow.ui.backend.entity.creditResolution.CreditResolutionCreditRequestDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class CreditResolutionCreditRequestDtoRestTemplate {
    private static RestTemplate restTemplate;
    private List<CreditResolutionCreditRequestDto> creditResolutionCreditRequestDtoList = new ArrayList<>();

    public CreditResolutionCreditRequestDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<CreditResolutionCreditRequestDto> getAll(){
        final String uri = "http://localhost:8080/rest//v1/creditResolutionCreditRequest/getAll";
        HttpEntity<CreditResolutionCreditRequestDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditResolutionCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditResolutionCreditRequestDto[].class);
        CreditResolutionCreditRequestDto[] result = response.getBody();
        creditResolutionCreditRequestDtoList = Arrays.asList(result);
        return creditResolutionCreditRequestDtoList;
    }

    public List<CreditResolutionCreditRequestDto> getByLogin(String login){
        final String uri = "http://localhost:8080/rest//v1/creditResolutionCreditRequest/getByLogin/{login}";
        Map<String,String> params = new HashMap<>();
        params.put("login",login);
        HttpEntity<CreditResolutionCreditRequestDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditResolutionCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,
                entity,CreditResolutionCreditRequestDto[].class,params);

        return Arrays.asList(response.getBody());
    }

    public List<CreditResolutionCreditRequestDto> getByCity(String city){
        final String uri = "http://localhost:8080/rest//v1/creditResolutionCreditRequest/getByCity/{city}";
        Map<String,String> params = new HashMap<>();
        params.put("city",city);
        HttpEntity<CreditResolutionCreditRequestDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditResolutionCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,
                entity,CreditResolutionCreditRequestDto[].class,params);

        return Arrays.asList(response.getBody());
    }
}

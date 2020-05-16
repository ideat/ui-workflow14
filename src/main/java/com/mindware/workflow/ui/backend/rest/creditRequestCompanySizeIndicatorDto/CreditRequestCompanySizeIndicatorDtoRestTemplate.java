package com.mindware.workflow.ui.backend.rest.creditRequestCompanySizeIndicatorDto;

import com.mindware.workflow.ui.backend.entity.dto.CreditRequestCompanySizeIndicatorDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CreditRequestCompanySizeIndicatorDtoRestTemplate {
    private  static RestTemplate restTemplate;

    private List<CreditRequestCompanySizeIndicatorDto> creditRequestCompanySizeIndicatorDtoList = new ArrayList<>();

    public CreditRequestCompanySizeIndicatorDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<CreditRequestCompanySizeIndicatorDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/creditRequestCompanySizeIndicator/getAll";
        HttpEntity<CreditRequestCompanySizeIndicatorDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditRequestCompanySizeIndicatorDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequestCompanySizeIndicatorDto[].class);
        CreditRequestCompanySizeIndicatorDto[] result = response.getBody();
        creditRequestCompanySizeIndicatorDtoList = Arrays.asList(result);
        return creditRequestCompanySizeIndicatorDtoList;
    }

    public List<CreditRequestCompanySizeIndicatorDto> getByUser(String loginUser){
        final String uri = "http://localhost:8080/rest/v1/creditRequestCompanySizeIndicator/getByUser";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("login-user",loginUser);
        HttpEntity<CreditRequestCompanySizeIndicatorDto> entity = new HttpEntity<>(headers);

        ResponseEntity<CreditRequestCompanySizeIndicatorDto[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,CreditRequestCompanySizeIndicatorDto[].class);
        return Arrays.asList(response.getBody());

    }

    public List<CreditRequestCompanySizeIndicatorDto> getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/creditRequestCompanySizeIndicator/getByCity";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("city",city);
        HttpEntity<CreditRequestCompanySizeIndicatorDto> entity = new HttpEntity<>(headers);

        ResponseEntity<CreditRequestCompanySizeIndicatorDto[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,CreditRequestCompanySizeIndicatorDto[].class);
        return Arrays.asList(response.getBody());

    }

    public byte[] report(String numberRequest){
        final String uri = "http://localhost:8080/rest/v1/creditRequestCompanySizeIndicator/report";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest);
        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

}

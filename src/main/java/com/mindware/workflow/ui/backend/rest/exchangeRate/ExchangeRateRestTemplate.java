package com.mindware.workflow.ui.backend.rest.exchangeRate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.ExchangeRate;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class ExchangeRateRestTemplate {
    private RestTemplate restTemplate;

    public ExchangeRateRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(ExchangeRate exchangeRate) throws JsonProcessingException {
        final String uri = "http://localhost:8080/rest/v1/exchangeRate/add";
        HttpEntity<ExchangeRate> entity = new HttpEntity<>(exchangeRate, HeaderJwt.getHeader());
        try{
            ResponseEntity<ExchangeRate> response = restTemplate.postForEntity(uri,entity,ExchangeRate.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseStsring = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseStsring,CustomError.class);
            return result;
        }
    }

    public List<ExchangeRate> getAll(){
        final String uri = "http://localhost:8080/rest/v1/exchangeRate/getAll";
        HttpEntity<ExchangeRate[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<ExchangeRate[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,ExchangeRate[].class);
        return Arrays.asList(response.getBody());
    }

    public ExchangeRate getActiveExchangeRateByCurrency(String currency){
        final String uri = "http://localhost:8080/rest/v1/exchangeRate/getActiveExchangeRateByCurrency";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("currency",currency);
        HttpEntity<ExchangeRate> entity = new HttpEntity<>(headers);
        ResponseEntity<ExchangeRate> response = restTemplate.exchange(uri,HttpMethod.GET,entity,ExchangeRate.class);
        return response.getBody();

    }
}

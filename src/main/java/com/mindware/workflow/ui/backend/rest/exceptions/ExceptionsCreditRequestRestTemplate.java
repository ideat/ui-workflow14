package com.mindware.workflow.ui.backend.rest.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.exceptions.ExceptionsCreditRequest;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ExceptionsCreditRequestRestTemplate {
    private static RestTemplate restTemplate;

    public ExceptionsCreditRequestRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(ExceptionsCreditRequest exceptionsCreditRequest) throws IOException {
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequest/add";
        HttpEntity<ExceptionsCreditRequest> entity = new HttpEntity<>(exceptionsCreditRequest, HeaderJwt.getHeader());
        try{
            ResponseEntity<ExceptionsCreditRequest> response = restTemplate.postForEntity(uri,entity,ExceptionsCreditRequest.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseStsring = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseStsring,CustomError.class);
            return result;
        }
    }

    public List<ExceptionsCreditRequest> getAll(){
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequest/getAll";
        HttpEntity<ExceptionsCreditRequest[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<ExceptionsCreditRequest[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,ExceptionsCreditRequest[].class);
        return Arrays.asList(response.getBody());
    }

    public List<ExceptionsCreditRequest> getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequest/getByNumberRequest";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest.toString());
        HttpEntity<ExceptionsCreditRequest[]> entity = new HttpEntity<>(headers);
        ResponseEntity<ExceptionsCreditRequest[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,ExceptionsCreditRequest[].class);
        return Arrays.asList(response.getBody());
    }

    public ExceptionsCreditRequest getByCodeExceptionNumberRequest(String codeException,Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequest/getByCodeExceptionNumberRequest";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest.toString());
        headers.set("code-exception",codeException);
        HttpEntity<ExceptionsCreditRequest> entity = new HttpEntity<>(headers);
        ResponseEntity<ExceptionsCreditRequest> response = restTemplate.exchange(uri,HttpMethod.GET,entity,ExceptionsCreditRequest.class);
        return response.getBody();
    }

    public void delete(UUID id){
        final String uri = "http://localhost:8080/rest/v1/exceptionsCreditRequest/delete";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id",id.toString());
        HttpEntity<?> request = new HttpEntity<>(headers);
        restTemplate.exchange(uri,HttpMethod.DELETE,request,Void.class);
    }
}

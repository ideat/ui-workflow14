package com.mindware.workflow.ui.backend.rest.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.exceptions.Exceptions;
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

public class ExceptionsRestTemplate {
    private static RestTemplate restTemplate;

    public ExceptionsRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(Exceptions exceptions) throws IOException {
        final String uri = "http://localhost:8080/rest/v1/exceptions/add";
        HttpEntity<Exceptions> entity = new HttpEntity<>(exceptions, HeaderJwt.getHeader());
        try{
            ResponseEntity<Exceptions> response = restTemplate.postForEntity(uri,entity,Exceptions.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseStsring = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseStsring,CustomError.class);
            return result;
        }


    }

    public Exceptions getByInternalCode(String code){
        final String uri = "http://localhost:8080/rest/v1/exceptions/getByInternalCode";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("internal-code",code);
        HttpEntity<Exceptions> entity = new HttpEntity<>(headers);
        ResponseEntity<Exceptions> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Exceptions.class);
        return response.getBody();
    }

    public List<Exceptions> getAll(){
        final String uri = "http://localhost:8080/rest/v1/exceptions/getAll";
        HttpEntity<Exceptions[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Exceptions[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Exceptions[].class);
        Exceptions[] exceptions = response.getBody();
        return Arrays.asList(exceptions);
    }
}

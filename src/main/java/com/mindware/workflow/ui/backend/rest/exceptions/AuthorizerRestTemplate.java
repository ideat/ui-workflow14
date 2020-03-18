package com.mindware.workflow.ui.backend.rest.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.exceptions.Authorizer;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class AuthorizerRestTemplate {
    private static RestTemplate restTemplate;

    public AuthorizerRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(Authorizer authorizer) throws JsonProcessingException {
        final String uri = "http://localhost:8080/rest/v1/authorizer/add";
        HttpEntity<Authorizer> entity = new HttpEntity<>(authorizer, HeaderJwt.getHeader());
        try {
            ResponseEntity<Authorizer> response = restTemplate.postForEntity(uri, entity, Authorizer.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseStsring = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseStsring,CustomError.class);
            return result;
        }
    }

    public List<Authorizer> getAll(){
        final String uri = "http://localhost:8080/rest/v1/authorizer/getAll";
        HttpEntity<Authorizer[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Authorizer[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Authorizer[].class);
        return Arrays.asList(response.getBody());
    }

    public Authorizer  getById(String id){
        final String uri = "http://localhost:8080/rest/v1/authorizer/getById";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("id",id);
        HttpEntity<Authorizer> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Authorizer> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Authorizer.class);
        return response.getBody();
    }

    public Authorizer  getByLoginUser(String loginuser){
        final String uri = "http://localhost:8080/rest/v1/authorizer/getByLoginUser";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("login-user",loginuser);
        HttpEntity<Authorizer> entity = new HttpEntity<>(headers);
        ResponseEntity<Authorizer> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Authorizer.class);
        return response.getBody();
    }


}

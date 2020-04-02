package com.mindware.workflow.ui.backend.rest.comercial.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.comercial.client.Client;
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
import java.util.UUID;

public class ClientRestTemplate {
    private static RestTemplate restTemplate;

    public ClientRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(Client client) throws JsonProcessingException {
        final String uri = "http://localhost:8080/rest/v1/comercial/client/add";
        HttpEntity<Client> entity = new HttpEntity<>(client, HeaderJwt.getHeader());
        try{
            ResponseEntity<Client> response = restTemplate.postForEntity(uri,entity,Client.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseString,CustomError.class);
            return result;
        }
    }

    public List<Client> getByUser(String loginUser){
        final String uri = "http://localhost:8080/rest/v1/comercial/client/getByUser";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("login-user",loginUser);
        HttpEntity<Client[]> entity = new HttpEntity<>(headers);
        ResponseEntity<Client[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Client[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Client> getAll(){
        final String uri = "http://localhost:8080/rest/v1/comercial/client/getAll";
        HttpEntity<Client[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Client[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,Client[].class);
        return Arrays.asList(response.getBody());
    }
}

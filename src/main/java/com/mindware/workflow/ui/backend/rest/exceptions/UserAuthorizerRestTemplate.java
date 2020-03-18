package com.mindware.workflow.ui.backend.rest.exceptions;

import com.mindware.workflow.ui.backend.entity.exceptions.UserAuthorizer;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class UserAuthorizerRestTemplate {
    private static RestTemplate restTemplate;

    public UserAuthorizerRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<UserAuthorizer> getAll(){
        final String uri ="http://localhost:8080/rest/v1/userAuthorizer/getAll";
        HttpHeaders headers = HeaderJwt.getHeader();
        HttpEntity<UserAuthorizer[]> entity = new HttpEntity<>(headers);

        ResponseEntity<UserAuthorizer[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,UserAuthorizer[].class);
        return Arrays.asList(response.getBody());
    }

    public List<UserAuthorizer> getByCity(String city){
        final String uri ="http://localhost:8080/rest/v1/userAuthorizer/getByCity";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);
        HttpEntity<UserAuthorizer[]> entity = new HttpEntity<>(headers);

        ResponseEntity<UserAuthorizer[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,UserAuthorizer[].class);
        return Arrays.asList(response.getBody());
    }
}

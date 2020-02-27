package com.mindware.workflow.ui.backend.rest.login;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginRestTemplate {
    private static RestTemplate restTemplate = new RestTemplate();

    public Token getToken(JwtRequest jwtRequest){
        final String uri = "http://localhost:8080/authenticate";
//        Token token = new Token();
        HttpEntity<JwtRequest> entity = new HttpEntity<>(jwtRequest);
        ResponseEntity<Token> response = restTemplate.postForEntity(uri,entity,Token.class);
        return response.getBody();
    }
}

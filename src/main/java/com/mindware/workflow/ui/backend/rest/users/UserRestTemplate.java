package com.mindware.workflow.ui.backend.rest.users;

import com.mindware.workflow.ui.backend.entity.Users;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class UserRestTemplate {
    RestTemplate restTemplate = new RestTemplate();

    public List<Users> getAllUsers(){
        final String uri = "http://localhost:8080/rest/user/v1/getAll";
        HttpEntity<Users[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users[].class);
        return Arrays.asList(response.getBody());
    }

    public Users add(Users users){
        final String uri = "http://localhost:8080/rest/user/v1/add";
        HttpEntity<Users> entity = new HttpEntity<>(users,HeaderJwt.getHeader());
        ResponseEntity<Users> response = restTemplate.postForEntity(uri,entity,Users.class);
        return response.getBody();
    }

    public Users getById(UUID id){
        final String uri = "http://localhost:8080/rest/user/v1/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<Users> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users.class,params);

        return response.getBody();
    }

    public Users getByIdUser(String login){
        final String uri = "http://localhost:8080/rest/user/v1/getByLogin/{login}";
        Map<String,String> params = new HashMap<>();
        params.put("login",login);
        HttpEntity<Users> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users.class,params);

        return response.getBody();
    }

    public List<Users> getByRol(String rol){
        final String uri = "http://localhost:8080/rest/user/v1/getByRol/{rol}";
        Map<String,String> params = new HashMap<>();
        params.put("rol",rol);
        HttpEntity<Users[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Users[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Users[].class,params);

        return Arrays.asList(response.getBody());
    }

}

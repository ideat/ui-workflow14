package com.mindware.workflow.ui.backend.rest.rol;

import com.mindware.workflow.ui.backend.entity.rol.Rol;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class RolRestTemplate {

    private static RestTemplate restTemplate = new RestTemplate();

    public Rol add(Rol rol){
        final String uri = "http://localhost:8080/rest/rol/v1/add";
        HttpEntity<Rol> entity = new HttpEntity<>(rol, HeaderJwt.getHeader());
        ResponseEntity<Rol> response = restTemplate.postForEntity(uri,entity,Rol.class);
        return response.getBody();
    }

    public List<Rol> getAllRols(){
        final String uri = "http://localhost:8080/rest/rol/v1/getAll";
        HttpEntity<Rol[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Rol[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Rol[].class);
        Rol[] rols = response.getBody();
        return Arrays.asList(rols);
    }

    public Rol getById(UUID id){
        final String uri = "http://localhost:8080/rest/rol/v1/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<Rol> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Rol> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Rol.class,params);

        return response.getBody();
    }

    public Rol getRolByName(String name){
        final String uri = "http://localhost:8080/rest/rol/v1/getByName/{name}";
        Map<String,String> params = new HashMap<>();
        params.put("name",name);
        HttpEntity<Rol> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Rol> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Rol.class,params);

        return response.getBody();
    }

    public void update(Rol rol){
        final String uri = "http://localhost:8080/rest/rol/v1/update";
        HttpEntity<Rol> entity = new HttpEntity<>(rol,HeaderJwt.getHeader());
        restTemplate.put(uri,entity);
    }

}

package com.mindware.workflow.ui.backend.rest.users;

import com.mindware.workflow.ui.backend.entity.users.Users;
import com.mindware.workflow.ui.backend.entity.users.UsersOfficeDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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

    public void updatePassword(Users users){
        final String uri = "http://localhost:8080/rest/user/v1/updatePassword";
        HttpEntity<Users> entity = new HttpEntity<>(users,HeaderJwt.getHeader());
        restTemplate.put(uri,entity);
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

    public String getPassword(String password){
        final String uri = "http://localhost:8080/rest/user/v1/getPassword";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("pass", password);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri,HttpMethod.GET,entity,String.class);
        return response.getBody();
    }

    public void updateUser(Users users){
        final String uri = "http://localhost:8080/rest/user/v1/updateUser";
        HttpEntity<Users> entity = new HttpEntity<>(users,HeaderJwt.getHeader());
        restTemplate.exchange(uri,HttpMethod.PUT,entity,Users.class);
    }


    public List<UsersOfficeDto> getByUserOfficeByCityAndRol(String city, String rol){
        final String uri = "http://localhost:8080/rest/userOffice/v1/getByCityAndRol/{city}/{rol}";
        Map<String,String> params = new HashMap<>();
        params.put("city",city);
        params.put("rol",rol);
        HttpEntity<UsersOfficeDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<UsersOfficeDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,UsersOfficeDto[].class,params);

        return Arrays.asList(response.getBody());
    }

    public List<UsersOfficeDto> getByUserOfficeByRol(String rol){
        final String uri = "http://localhost:8080/rest/userOffice/v1/getByRol/{rol}";
        Map<String,String> params = new HashMap<>();
        params.put("rol",rol);
        HttpEntity<UsersOfficeDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<UsersOfficeDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,UsersOfficeDto[].class,params);

        return Arrays.asList(response.getBody());
    }
}

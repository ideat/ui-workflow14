package com.mindware.workflow.ui.backend.rest.parameter;

import com.mindware.workflow.ui.backend.entity.config.Parameter;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class ParameterRestTemplate {
    private static RestTemplate restTemplate = new RestTemplate();
    private List<Parameter> parameterList = new ArrayList<>();

    public ParameterRestTemplate(){ }

    public List<Parameter> getAllParameter(){
        final String uri = "http://localhost:8080/rest/v1/parameter/getAll";
        HttpEntity<Parameter[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Parameter[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Parameter[].class);
//        Parameter[] parameters = restTemplate.getForObject(uri,Parameter[].class);
        parameterList =  Arrays.asList(response.getBody());
        return parameterList;
    }

    public Parameter addParameter(Parameter parameter){
        final String uri = "http://localhost:8080/rest/v1/parameter/add";
        HttpEntity<Parameter> entity = new HttpEntity<>(parameter,HeaderJwt.getHeader());
        ResponseEntity<Parameter> response = restTemplate.postForEntity(uri,entity,Parameter.class);
        return response.getBody();
    }

    public List<Parameter> getParametersByCategory(String category){
        final String uri = "http://localhost:8080/rest/v1/parameter/getParameterByCategory/{category}";
        Map<String,String> params = new HashMap<>();
        params.put("category",category);
        HttpEntity<Parameter[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Parameter[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Parameter[].class,params);
        return Arrays.asList(response.getBody()); // Arrays.asList(restTemplate.getForObject(uri,Parameter[].class,params));
    }

    public List<Parameter> getParametersByCategories(String category){
        final String uri = "http://localhost:8080/rest/v1/parameter/getParameterByCategories/{category}";
        Map<String,String> params = new HashMap<>();
        params.put("category",category);
        HttpEntity<Parameter[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Parameter[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Parameter[].class,params);
        return Arrays.asList(response.getBody()); // Arrays.asList(restTemplate.getForObject(uri,Parameter[].class,params));
    }
}

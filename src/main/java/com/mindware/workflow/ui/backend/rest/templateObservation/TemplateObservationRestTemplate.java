package com.mindware.workflow.ui.backend.rest.templateObservation;

import com.mindware.workflow.ui.backend.entity.templateObservation.TemplateObservation;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TemplateObservationRestTemplate {
    private static RestTemplate restTemplate;

    public TemplateObservationRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public TemplateObservation add(TemplateObservation templateObservation){
        final String uri = "http://localhost:8080/rest/v1/templateObservation/add";
        HttpEntity<TemplateObservation> entity = new HttpEntity<>(templateObservation, HeaderJwt.getHeader());
        ResponseEntity<TemplateObservation> response = restTemplate.postForEntity(uri,entity,TemplateObservation.class);
        return response.getBody();
    }

    public List<TemplateObservation> getByTask(String task){
        final String uri = "http://localhost:8080/rest/v1/templateObservation/getByTask/{task}";
        Map<String,String> params = new HashMap<>();
        params.put("task",task);
        HttpEntity<TemplateObservation[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateObservation[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateObservation[].class,params);

        return new ArrayList<>(Arrays.asList(response.getBody()));
    }

    public List<TemplateObservation> getAll(){
        final String uri = "http://localhost:8080/rest/v1/templateObservation/getAll";
        HttpEntity<TemplateObservation[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateObservation[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateObservation[].class);
        return Arrays.asList(response.getBody());
    }

    public void delete(String id){
        final String uri = "http://localhost:8080/rest/v1/templateObservation/delete/{id}";
        Map<String,String> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<?> request = new HttpEntity<Object>(HeaderJwt.getHeader());
        restTemplate.exchange(uri, HttpMethod.DELETE,request,Void.class,params);
    }


}

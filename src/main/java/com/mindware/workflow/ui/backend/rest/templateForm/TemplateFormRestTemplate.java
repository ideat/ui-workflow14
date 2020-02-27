package com.mindware.workflow.ui.backend.rest.templateForm;

import com.mindware.workflow.ui.backend.entity.config.TemplateForm;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class TemplateFormRestTemplate {
    private static RestTemplate restTemplate;

    public TemplateFormRestTemplate(){
        restTemplate = new RestTemplate();

    }

    public List<TemplateForm> getAll(){
        final String uri = "http://localhost:8080/rest/v1/templateforms/getAll";
        HttpEntity<TemplateForm[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateForm[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateForm[].class);

        return  Arrays.asList(response.getBody());
    }

    public Optional<TemplateForm> getByNameAndCategory(String name, String category){
        final String uri = "http://localhost:8080/rest/v1/templateforms/getByNameCategory/{name}/{category}";
        Map<String,String> params = new HashMap<>();
        params.put("name",name);
        params.put("category",category);
        HttpEntity<Optional<TemplateForm>> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateForm> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateForm.class,params);

        return Optional.ofNullable(response.getBody());
    }

    public TemplateForm add(TemplateForm templateForm){
        final String uri = "http://localhost:8080/rest/v1/templateforms/add";
        HttpEntity<TemplateForm> entity = new HttpEntity<>(templateForm,HeaderJwt.getHeader());
        ResponseEntity<TemplateForm> response = restTemplate.postForEntity(uri,entity,TemplateForm.class);
        return response.getBody();
    }

    public void updateFieldStructure(TemplateForm templateForm){
        final String uri = "http://localhost:8080/rest/v1/templateforms/updateFieldStructure";
        HttpEntity<TemplateForm> entity = new HttpEntity<>(templateForm,HeaderJwt.getHeader());
        restTemplate.put(uri,entity);
    }

}

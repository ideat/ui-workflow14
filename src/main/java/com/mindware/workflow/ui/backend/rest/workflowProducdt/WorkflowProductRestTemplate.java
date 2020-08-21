package com.mindware.workflow.ui.backend.rest.workflowProducdt;

import com.mindware.workflow.ui.backend.entity.config.WorkflowProduct;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkflowProductRestTemplate {
    private static RestTemplate restTemplate = new RestTemplate();

    public WorkflowProduct add(WorkflowProduct workflowProduct){
        final String uri ="http://localhost:8080/rest/v1/workflowproduct/add";
        HttpEntity<WorkflowProduct> entity = new HttpEntity<>(workflowProduct, HeaderJwt.getHeader());
        ResponseEntity<WorkflowProduct> response = restTemplate.postForEntity(uri,entity,WorkflowProduct.class);
        return response.getBody();
    }

    public WorkflowProduct getByCode(String code){
        final String uri ="http://localhost:8080/rest/v1/workflowproduct/getByCode/{code}";
        Map<String,String> params = new HashMap<>();
        params.put("code",code);
        HttpEntity<WorkflowProduct> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<WorkflowProduct> response = restTemplate.exchange(uri, HttpMethod.GET,entity,WorkflowProduct.class,params);
        return response.getBody();
    }

    public WorkflowProduct getByTypeCreditAndObject(String codeTypeCredit, String codeObjectCredit){
        final String uri ="http://localhost:8080/rest/v1/workflowproduct/getByTypeCreditAndObject/{codetypecredit}/{codeobjectcredit}";
        Map<String,String> params = new HashMap<>();
        params.put("codetypecredit",codeTypeCredit);
        params.put("codeobjectcredit",codeObjectCredit);
        HttpEntity<WorkflowProduct> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<WorkflowProduct> response = restTemplate.exchange(uri, HttpMethod.GET,entity,WorkflowProduct.class,params);
        return response.getBody();
    }

    public List<WorkflowProduct> getAll(){
        final String uri ="http://localhost:8080/rest/v1/workflowproduct/getAll";
        HttpEntity<WorkflowProduct[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<WorkflowProduct[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,WorkflowProduct[].class);
        return Arrays.asList(response.getBody());

    }

}

package com.mindware.workflow.ui.backend.rest.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.contract.ContractVariable;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ContractVariableRestTemplate {
    private static RestTemplate restTemplate;

    public ContractVariableRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(ContractVariable contractVariable) throws JsonProcessingException {
        final String uri = "http://localhost:8080/rest/v1/contractVariable/add";
        HttpEntity<ContractVariable> entity = new HttpEntity<>(contractVariable, HeaderJwt.getHeader());
        try{
            ResponseEntity<ContractVariable> response = restTemplate.postForEntity(uri,entity,ContractVariable.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseString,CustomError.class);
            return result;
        }
    }

    public List<ContractVariable> getAll(){
        final String uri = "http://localhost:8080/rest/v1/contractVariable/getAll";
        HttpEntity<ContractVariable[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<ContractVariable[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,ContractVariable[].class);
        return Arrays.asList(response.getBody());
    }

    public void delete(String id){
        final String uri = "http://localhost:8080/rest/v1/contractVariable/delete";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id",id);
        HttpEntity<?> request = new HttpEntity<>(headers);
        restTemplate.exchange(uri,HttpMethod.DELETE,request,Void.class);
    }
}

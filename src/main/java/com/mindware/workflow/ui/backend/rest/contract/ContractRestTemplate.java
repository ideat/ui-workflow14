package com.mindware.workflow.ui.backend.rest.contract;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.contract.Contract;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import com.vaadin.flow.component.grid.HeaderRow;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class ContractRestTemplate {
    private static RestTemplate restTemplate;

    public ContractRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(Contract contract) throws JsonProcessingException {
        final String uri = "http://localhost:8080/rest/v1/contract/add";
        HttpEntity<Contract> entity = new HttpEntity<>(contract, HeaderJwt.getHeader());
        try{
            ResponseEntity<Contract> response = restTemplate.postForEntity(uri,entity,Contract.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseString,CustomError.class);
            return result;
        }
    }

    public Contract getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/contract/getByNumberRequest";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest.toString());
        HttpEntity<Contract> entity  = new HttpEntity<>(headers);
        ResponseEntity<Contract> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Contract.class);
        return response.getBody();
    }
}

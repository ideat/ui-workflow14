package com.mindware.workflow.ui.backend.rest.cashFlow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.cashFlow.CashFlow;
import com.mindware.workflow.ui.backend.entity.cashFlow.FlowItem;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class CashFlowRestTemplate {
    private static RestTemplate restTemplate;

    public CashFlowRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add (CashFlow cashFlow) throws JsonProcessingException {
        final String uri = "http://localhost:8080/rest/v1/cashFlow/add";
        HttpEntity<CashFlow> entity = new HttpEntity<>(cashFlow, HeaderJwt.getHeader());
        try {
            ResponseEntity<CashFlow> response = restTemplate.postForEntity(uri, entity, CashFlow.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseString,CustomError.class);
            return result;
        }
    }

    public CashFlow getById(UUID id){
        final String uri = "http://localhost:8080/rest/v1/cashFlow/getById/{idcashflow}";
        Map<String,UUID> params = new HashMap<>();
        params.put("idcashflow",id);
        HttpEntity<CashFlow> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CashFlow> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CashFlow.class,params);
        return response.getBody();
    }

    public CashFlow getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/cashFlow/getByNumberRequest/{numberRequest}";
        Map<String,Integer> params = new HashMap<>();
        params.put("numberRequest",numberRequest);
        HttpEntity<CashFlow> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CashFlow> response = restTemplate.exchange(uri,HttpMethod.GET,entity,CashFlow.class,params);
        return response.getBody();
    }

    public List<FlowItem> getGenerateCashFlow(Integer numberRequest, UUID idCreditRequestApplicant){
        final String uri = "http://localhost:8080/rest/v1/cashFlow/generateCashFlow";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.set("numberrequest",numberRequest.toString());
        headers.set("id",idCreditRequestApplicant.toString());
        HttpEntity<FlowItem[]> entity = new HttpEntity<>(headers);

        ResponseEntity<FlowItem[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,FlowItem[].class);

        return Arrays.asList(response.getBody());
    }

}

package com.mindware.workflow.ui.backend.rest.stageHistory;

import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistory;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;

@Service
public class StageHistoryRestTemplate {
    private static RestTemplate restTemplate = new RestTemplate();


    public StageHistory add(StageHistory stageHistory){
        final String uri = "http://localhost:8080/rest/v1/stageHistory/add";
        HttpEntity<StageHistory> entity = new HttpEntity<>(stageHistory, HeaderJwt.getHeader());
        ResponseEntity<StageHistory> response = restTemplate.postForEntity(uri,entity,StageHistory.class);
        return response.getBody();
    }

    public List<StageHistory> getAll(){
        final String uri = "http://localhost:8080/rest/v1/stageHistory/getAll";
        HttpEntity<StageHistory[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<StageHistory[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistory[].class);
        StageHistory[] stageHistories = response.getBody();
        return Arrays.asList(stageHistories);
    }

    public List<StageHistory> getByNumberRequestStageState(String numberRequest, String state, String stage){
        final String uri = "http://localhost:8080/rest/v1/stageHistory/getByNumberRequestStageState";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("number-request",numberRequest);
        headers.add("stage",stage);
        headers.add("state",state);
        HttpEntity<StageHistory> entity = new HttpEntity<>(headers);
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        ResponseEntity<StageHistory[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistory[].class);

        return Arrays.asList(response.getBody());
    }

    public List<StageHistory> getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/stageHistory/getByNumberRequest/{number_request}";
        Map<String,Integer> params = new HashMap<>();
        params.put("number_request",numberRequest);
        HttpEntity<StageHistory[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<StageHistory[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistory[].class,params);

        return  Arrays.asList(response.getBody());
    }

    public StageHistory getById(UUID id){
        final String uri = "http://localhost:8080/rest/v1/stageHistory/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<StageHistory> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<StageHistory> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistory.class,params);

        return response.getBody();
    }

    public void update(StageHistory stageHistory){
        final String uri = "http://localhost:8080/rest/v1/stageHistory/update";
        HttpEntity<StageHistory> entity = new HttpEntity<>(stageHistory,HeaderJwt.getHeader());
        restTemplate.put(uri,entity);

    }
}

package com.mindware.workflow.ui.backend.rest.observation;

import com.mindware.workflow.ui.backend.entity.observation.Observation;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class ObservationRestTemplate {
    private static RestTemplate restTemplate;

    public ObservationRestTemplate(){
        restTemplate = new RestTemplate();
    }
    public Observation add(Observation observation){
        final String uri = "http://localhost:8080/rest/v1/observation/add";
        HttpEntity<Observation> entity = new HttpEntity<>(observation, HeaderJwt.getHeader());
        ResponseEntity<Observation> response = restTemplate.postForEntity(uri,entity,Observation.class);
        return response.getBody();
    }

    public List<Observation> getAll(){
        final String uri = "http://localhost:8080/rest/v1/observation/getAll";
        Observation[] observations = restTemplate.getForObject(uri,Observation[].class,HeaderJwt.getHeader());
        return Arrays.asList(observations);
    }

    public List<Observation> getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/observation/getByNumberRequest/{number_request}";
        Map<String,Integer> params = new HashMap<>();
        params.put("number_request",numberRequest);
        return Arrays.asList(restTemplate.getForObject(uri,Observation[].class,params,HeaderJwt.getHeader()));
    }

    public List<Observation> getById(UUID id){
        final String uri = "http://localhost:8080/rest/v1/observation/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        return Arrays.asList(restTemplate.getForObject(uri,Observation[].class,params,HeaderJwt.getHeader()));
    }

    public List<Observation> getByNumberApplicant(Integer numberApplicant){
        final String uri = "http://localhost:8080/rest/v1/observation/getByNumberApplicant/{number_applicant}";
        Map<String,Integer> params = new HashMap<>();
        params.put("number_applicant",numberApplicant);
        HttpEntity<Observation[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Observation[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Observation[].class,params);
        return Arrays.asList(response.getBody());
    }

    public Observation getByNumberRequestApplicantTask(Integer numberRequest, String task){
        final String uri = "http://localhost:8080/rest/v1/observation/getByNumberRequestApplicantTask";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.add("number_request",numberRequest.toString());
        headers.add("task",task);
        HttpEntity<Observation> entity = new HttpEntity<>(headers);

        ResponseEntity<Observation> response = restTemplate.exchange(uri, HttpMethod.GET, entity, Observation.class);
        return response.getBody();
    }

    public byte[] report(String numberRequest, String numberApplicant, String task){
        final String uri = "http://localhost:8080/rest/v1/observationCreditAnalysis";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest);
        headers.set("number-applicant",numberApplicant);
        headers.set("task",task);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();

    }

}

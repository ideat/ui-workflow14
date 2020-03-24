package com.mindware.workflow.ui.backend.rest.observationCreditRequestApplicant;

import com.mindware.workflow.ui.backend.entity.observation.dto.ObservationCreditRequestApplicant;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ObservationCreditRequestApplicantRestTemplate {
    private static RestTemplate restTemplate;
    private List<ObservationCreditRequestApplicant> observationCreditRequestApplicantList = new ArrayList<>();

    public ObservationCreditRequestApplicantRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<ObservationCreditRequestApplicant> getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/observationCreditRequestApplicant/getByCity/{city}";
        Map<String,String> params = new HashMap<>();
        params.put("city",city);
        HttpEntity<ObservationCreditRequestApplicant[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<ObservationCreditRequestApplicant[]> response = restTemplate.exchange(uri, HttpMethod.GET,
                entity,ObservationCreditRequestApplicant[].class,params);

        ObservationCreditRequestApplicant[] observationCreditRequestApplicants = response.getBody();
        return Arrays.asList(observationCreditRequestApplicants);
    }

    public List<ObservationCreditRequestApplicant> getByUser(String login){
        final String uri = "http://localhost:8080/rest/v1/observationCreditRequestApplicant/getByLogin/{login}";
        Map<String,String> params = new HashMap<>();
        params.put("login",login);
        HttpEntity<ObservationCreditRequestApplicant[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<ObservationCreditRequestApplicant[]> response = restTemplate.exchange(uri, HttpMethod.GET,
                entity,ObservationCreditRequestApplicant[].class,params);

        ObservationCreditRequestApplicant[] observationCreditRequestApplicants = response.getBody();
        return Arrays.asList(observationCreditRequestApplicants);
    }

    public List<ObservationCreditRequestApplicant> getAll(){
        final String uri = "http://localhost:8080/rest/v1/observationCreditRequestApplicant/getAll";
        HttpEntity<ObservationCreditRequestApplicant[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<ObservationCreditRequestApplicant[]> response = restTemplate.exchange(uri,HttpMethod.GET,
                entity,ObservationCreditRequestApplicant[].class);
        return Arrays.asList(response.getBody());
    }

}

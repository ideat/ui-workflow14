package com.mindware.workflow.ui.backend.rest.applicant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.Applicant;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
public class ApplicantRestTemplate {

    private static RestTemplate restTemplate;
    private List<Applicant> applicantList = new ArrayList<>();

    public ApplicantRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<Applicant> getAllApplicants(){
        final String uri = "http://localhost:8080/rest/v1/applicant/getAll";
        HttpEntity<Applicant[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Applicant[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Applicant[].class);
//        Applicant[] applicants = restTemplate.getForObject(uri,Applicant[].class);
        Applicant[] applicants = response.getBody();
        return Arrays.asList(applicants);

    }

    public Object addApplicant(Applicant applicant) throws IOException {
        final String uri = "http://localhost:8080/rest/v1/applicant/add";

        HttpEntity<Applicant> entity = new HttpEntity<>(applicant,HeaderJwt.getHeader());
        try {
            ResponseEntity<Applicant> response = restTemplate.postForEntity(uri, entity, Applicant.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();

            ObjectMapper mapper = new ObjectMapper();

            CustomError result = mapper.readValue(responseString,
                    CustomError.class);
            return result;
        }

    }

    public void updateApplicant(Applicant applicant){
        final String uri = "http://localhost:8080/rest/v1/applicant/update";
        HttpEntity<Applicant> entity = new HttpEntity<>(applicant,HeaderJwt.getHeader());
        restTemplate.put(uri,entity);
    }

    public Applicant getApplicantById(UUID id){
        final String uri = "http://localhost:8080/rest/v1/applicant/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<Applicant> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<Applicant> response = restTemplate.exchange(uri, HttpMethod.GET,entity,Applicant.class,params);
        return response.getBody();

    }
}

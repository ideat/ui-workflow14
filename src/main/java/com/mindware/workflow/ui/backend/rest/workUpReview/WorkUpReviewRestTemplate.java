package com.mindware.workflow.ui.backend.rest.workUpReview;

import com.mindware.workflow.ui.backend.entity.workUpReview.WorkUpReview;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class WorkUpReviewRestTemplate {
    private static RestTemplate restTemplate;

    public WorkUpReviewRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public WorkUpReview add(WorkUpReview workUpReview){
        final String uri = "http://localhost:8080/rest/v1/workUpReview/add";
        HttpEntity<WorkUpReview> entity = new HttpEntity<>(workUpReview, HeaderJwt.getHeader());
        ResponseEntity<WorkUpReview> response = restTemplate.postForEntity(uri,entity,WorkUpReview.class);
        return response.getBody();
    }

    public List<WorkUpReview> getAllWorkUpReviews(){
        final String uri = "http://localhost:8080/rest/v1/workUpReview/getAllWorkUpReviews";
        HttpEntity<WorkUpReview[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<WorkUpReview[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,WorkUpReview[].class);
        return Arrays.asList(response.getBody());
    }

    public List<WorkUpReview> getWorkUpReviewByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/workUpReview/getWorkUpReviewByNumberRequest";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("number-request", numberRequest.toString());
        HttpEntity<WorkUpReview> entity = new HttpEntity<>(headers);

        ResponseEntity<WorkUpReview[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,WorkUpReview[].class);

        return Arrays.asList(response.getBody());
    }

    public WorkUpReview getWorkUpReviewById(UUID idWorkUpReview){
        final String uri = "http://localhost:8080/rest/v1/workUpReview/getWorkUpReviewById";
        HttpHeaders headers =HeaderJwt.getHeader();
        headers.add("id-workup-preview",idWorkUpReview.toString());
        HttpEntity<WorkUpReview> entity = new HttpEntity<>(headers);

        ResponseEntity<WorkUpReview> response = restTemplate.exchange(uri, HttpMethod.GET,entity,WorkUpReview.class);
        return response.getBody();
    }


}

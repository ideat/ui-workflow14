package com.mindware.workflow.ui.backend.rest.stageHistoryCreditRequestDto;

import com.mindware.workflow.ui.backend.entity.stageHistory.StageHistoryCreditRequestDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class StageHistoryCreditRequestDtoRestTemplate {
    private static RestTemplate restTemplate = new RestTemplate();

    public List<StageHistoryCreditRequestDto> getByUserNumberRequest(String user, Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getByUserNumberRequest";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("user",user);
        headers.set("number-request",numberRequest.toString());

        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getByUserNumberRequestState(String user, Integer numberRequest, String state){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getByUserNumberRequestState";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("user",user);
        headers.set("number-request",numberRequest.toString());
        headers.set("state",state);

        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getByCity";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);

        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getAll";

        StageHistoryCreditRequestDto[] stageHistoryCreditRequestDtos = restTemplate.getForObject(uri,StageHistoryCreditRequestDto[].class,HeaderJwt.getHeader());

        return Arrays.asList(stageHistoryCreditRequestDtos);
    }

    public List<StageHistoryCreditRequestDto> getByUserRolState(String user, String state, String rol){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getByUserRolState";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("user",user);
        headers.set("state",state);
        headers.set("rol",rol);

        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getByStateRol( String state, String rol){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getByStateRol";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("state",state);
        headers.set("rol",rol);

        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getDetailByUserRol(String user, String rol, String state){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getDetailByUserRol";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("user",user);
        headers.set("rol",rol);
        headers.set("state",state);

        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getResumByUserRol(String user, String rol, String state, String city){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getResumByUserRol";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("user",user);
        headers.set("rol",rol);
        headers.set("state",state);
        headers.set("city",city);
        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate
                .exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getGlobalDetailByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getGlobalDetailByCity";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);
        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate
                .exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getGlobalDetailByUser(String loginUser){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getGlobalDetailByUser";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("user",loginUser);
        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate
                .exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getGlobalDetail(){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getGlobalDetail";

        HttpHeaders headers = HeaderJwt.getHeader();
        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate
                .exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<StageHistoryCreditRequestDto> getDetailByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/stageHistoryCreditRequest/getDetailByNumberRequest";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("number-request",numberRequest.toString());
        HttpEntity<StageHistoryCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<StageHistoryCreditRequestDto[]> response = restTemplate
                .exchange(uri, HttpMethod.GET,entity,StageHistoryCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }
}

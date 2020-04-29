package com.mindware.workflow.ui.backend.rest.cityProvince;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.CityProvince;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.function.ServerRequest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CityProvinceRestTemplate {
    private static RestTemplate restTemplate;

    public CityProvinceRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(CityProvince cityProvince) throws JsonProcessingException {
        final String uri = "http://localhost:8080/rest/v1/cityProvince/add";
        HttpEntity<CityProvince> entity = new HttpEntity<>(cityProvince, HeaderJwt.getHeader());
        try {
            ResponseEntity<CityProvince> response = restTemplate.postForEntity(uri, entity, CityProvince.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseString,CustomError.class);
            return result;
        }
    }

    public List<CityProvince> getAll(){
        final String uri = "http://localhost:8080/rest/v1/cityProvince/getAll";
        HttpEntity<CityProvince[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CityProvince[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CityProvince[].class);
        return Arrays.asList(response.getBody());
    }

    public CityProvince getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/cityProvince/getByCity";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);
        HttpEntity<CityProvince> entity = new HttpEntity<>(headers);
        ResponseEntity<CityProvince> response = restTemplate.exchange(uri,HttpMethod.GET,entity,CityProvince.class);
        return response.getBody();
    }

    public void delete(String id){
        final String uri = "http://localhost:8080/rest/v1/cityProvince/delete";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id",id);
        HttpEntity<?> request = new HttpEntity<>(headers);
        restTemplate.exchange(uri,HttpMethod.DELETE,request,Void.class);
    }

}

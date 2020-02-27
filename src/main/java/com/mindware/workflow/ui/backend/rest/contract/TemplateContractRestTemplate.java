package com.mindware.workflow.ui.backend.rest.contract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.contract.TemplateContract;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.*;

public class TemplateContractRestTemplate {
    private static RestTemplate restTemplate;

    public TemplateContractRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public Object add(TemplateContract templateContract) throws JsonProcessingException {
        final String uri ="http://localhost:8080/rest/v1/templateContract/add";
        HttpEntity<TemplateContract> entity = new HttpEntity<>(templateContract, HeaderJwt.getHeader());
        try{
            ResponseEntity<TemplateContract> response = restTemplate.postForEntity(uri,entity,TemplateContract.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseString,CustomError.class);
            return result;
        }

    }

    public String  upload( String pathFileTemp, String fileName) {
        final String uri ="http://localhost:8080/rest/v1/templateContract/upload";
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        FileSystemResource value = new FileSystemResource(new File(pathFileTemp));
        bodyMap.add("file",value);
        bodyMap.add("filename",fileName);

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String,Object>> request = new HttpEntity<>(bodyMap,headers);

        ResponseEntity<String> reponse = restTemplate.exchange(uri, HttpMethod.POST,request,String.class);
        return reponse.getBody();
    }

    public List<TemplateContract> getAll(){
        final String uri ="http://localhost:8080/rest/v1/templateContract/getAll";
        HttpEntity<TemplateContract[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateContract[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateContract[].class);
        TemplateContract[] templateContracts = response.getBody();
        return Arrays.asList(templateContracts);

    }

    public List<TemplateContract> getAllActive(String active){
        final String uri ="http://localhost:8080/rest/v1/templateContract/getAllActive/{active}";
        Map<String,String> params = new HashMap<>();
        params.put("active",active);
        HttpEntity<TemplateContract[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateContract[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateContract[].class,params);
        TemplateContract[] templateContracts = response.getBody();
        return Arrays.asList(templateContracts);

    }

    public TemplateContract getById(UUID id){
        final String uri ="http://localhost:8080/rest/v1/templateContract/getById/{id}";
        Map<String,UUID> params = new HashMap<>();
        params.put("id",id);
        HttpEntity<TemplateContract> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateContract> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateContract.class,params);
        return response.getBody();

    }

    public TemplateContract getByFileName(String fileName){
        final String uri ="http://localhost:8080/rest/v1/templateContract/getByFileName/{filename}";
        Map<String,String> params = new HashMap<>();
        params.put("filename",fileName);
        HttpEntity<TemplateContract> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TemplateContract> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TemplateContract.class,params);
        return response.getBody();

    }



}

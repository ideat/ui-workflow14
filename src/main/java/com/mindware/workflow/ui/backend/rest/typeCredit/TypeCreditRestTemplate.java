package com.mindware.workflow.ui.backend.rest.typeCredit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mindware.workflow.ui.backend.entity.config.TypeCredit;
import com.mindware.workflow.ui.backend.exception.CustomError;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TypeCreditRestTemplate {
    private static RestTemplate restTemplate = new RestTemplate();

    public Object add(TypeCredit typeCredit) throws JsonProcessingException {
        final String uri ="http://localhost:8080/rest/v1/typeCredit/add";
        HttpEntity<TypeCredit> entity = new HttpEntity<>(typeCredit, HeaderJwt.getHeader());
        try {
            ResponseEntity<TypeCredit> response = restTemplate.postForEntity(uri, entity, TypeCredit.class);
            return response.getBody();
        }catch (HttpStatusCodeException e){
            String responseString = e.getResponseBodyAsString();
            ObjectMapper mapper = new ObjectMapper();
            CustomError result = mapper.readValue(responseString,CustomError.class);
            return result;
        }

    }

    public TypeCredit getByExternalCode(String externalCode){
        final String uri ="http://localhost:8080/rest/v1/typeCredit/getByExternalCode/{code}";
        Map<String,String> params = new HashMap<>();
        params.put("code",externalCode);
        HttpEntity<TypeCredit> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TypeCredit> response = restTemplate.exchange(uri, HttpMethod.GET,entity,TypeCredit.class,params);
        return response.getBody();
    }



    public List<TypeCredit> getAll(){
        final String uri ="http://localhost:8080/rest/v1/typeCredit/getAll";
        HttpEntity<TypeCredit[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<TypeCredit[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,TypeCredit[].class);
        return Arrays.asList(response.getBody());
    }
}

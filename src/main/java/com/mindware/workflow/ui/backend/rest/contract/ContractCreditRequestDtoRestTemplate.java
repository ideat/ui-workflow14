package com.mindware.workflow.ui.backend.rest.contract;

import com.mindware.workflow.ui.backend.entity.contract.ContractCreditRequestDto;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ContractCreditRequestDtoRestTemplate {

    private static RestTemplate restTemplate;

    public ContractCreditRequestDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public List<ContractCreditRequestDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/contractCreditRequestDto/getAll";
        HttpHeaders headers = HeaderJwt.getHeader();
        HttpEntity<ContractCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<ContractCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,ContractCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<ContractCreditRequestDto> getByCity(String city){
        final String uri = "http://localhost:8080/rest/v1/contractCreditRequestDto/getByCity";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("city",city);
        HttpEntity<ContractCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<ContractCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,ContractCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public List<ContractCreditRequestDto> getByUser(String loginUser){
        final String uri = "http://localhost:8080/rest/v1/contractCreditRequestDto/getByUser";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("login-user",loginUser);
        HttpEntity<ContractCreditRequestDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<ContractCreditRequestDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,ContractCreditRequestDto[].class);
        return Arrays.asList(response.getBody());
    }

    public void getContractFile(String pathContract,String pathContractDownloaded) throws IOException {
        final String uri = "http://localhost:8080/rest/v1/contractCreditRequestDto/getFileContract";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("path-contract",pathContract);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);


        Files.write(Paths.get(pathContractDownloaded+ File.separator+"test.docx"),response.getBody());
    }
}

package com.mindware.workflow.ui.backend.rest.patrimonialStatement;

import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

public class PatrimonialStatementDtoRestTemplate {
    private static RestTemplate restTemplate;

    public PatrimonialStatementDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    public byte[] reportSales(String idCreditRequestApplicant,String category, String activity,String idApplicant, String idPatrimonialStatement){
        final String uri = "http://localhost:8080/rest/v1/patrimonialStatementSalesReport";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-credit-request-applicant", idCreditRequestApplicant);
        headers.set("category", category);
        headers.set("activity",activity);
        headers.set("id-applicant",idApplicant);
        headers.set("id-patrimonial-statement",idPatrimonialStatement);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();

    }

    public byte[] reportVaeIndependent(String idPatrimonialStatement, String idApplicant){
        final String uri = "http://localhost:8080/rest/v1/patrimonialStatementVaeIndependentReport";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-applicant", idApplicant);
        headers.set("id-patrimonial-statement",idPatrimonialStatement);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

    public Double getOperativeEarningVae(String idPatrimonialStatement, String idApplicant){
        final String uri = "http://localhost:8080/rest/v1/getOperativeEarningVaeIndependent";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-applicant", idApplicant);
        headers.set("id-patrimonial-statement",idPatrimonialStatement);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET,entity,String.class);
        String result = response.getBody();
        return Double.parseDouble(result);
    }

    public Double getSalaryVaeDependent(String idApplicant, String idPatrimonialStatement, String typeClient){
        final String uri = "http://localhost:8080/rest/v1/salaryVaeDependent";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-applicant",idApplicant);
        headers.set("id-patrimonial-statement",idPatrimonialStatement);
        headers.set("type-client",typeClient);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(uri,HttpMethod.GET,entity,String.class);
        String result = response.getBody();
        return Double.parseDouble(result);

    }

    public byte[] reportCostProduct(String product,String idPatrimonialStatement, String idApplicant){
        final String uri = "http://localhost:8080/rest/v1/costProductReport";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("product",product);
        headers.set("id-applicant", idApplicant);
        headers.set("id-patrimonial-statement",idPatrimonialStatement);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

    public byte[] reportInventoryProduction(String idPatrimonialStatement, String idApplicant ){
        final String uri = "http://localhost:8080/rest/v1/productionInventoryReport";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-applicant", idApplicant);
        headers.set("id-patrimonial-statement",idPatrimonialStatement);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

    public byte[] reportInventorySales(String idPatrimonialStatement, String idApplicant ){
        final String uri = "http://localhost:8080/rest/v1/salesInventoryReport";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-applicant", idApplicant);
        headers.set("id-patrimonial-statement",idPatrimonialStatement);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

    public byte[] reportVaeDependent(String idApplicant,  String idCreditRequestApplicant
                                    , String numberRequest){
        final String uri = "http://localhost:8080/rest/v1/vaeDependentReport";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.set("id-applicant",idApplicant);
        headers.set("id-creditrequest-applicant",idCreditRequestApplicant);
        headers.set("number-request",numberRequest);

        HttpEntity<byte[]> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(uri,HttpMethod.GET,entity,byte[].class);
        return response.getBody();
    }

}

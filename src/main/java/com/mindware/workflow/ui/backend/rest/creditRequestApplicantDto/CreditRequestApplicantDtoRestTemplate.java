package com.mindware.workflow.ui.backend.rest.creditRequestApplicantDto;

import com.mindware.workflow.ui.backend.entity.dto.CreditRequestApplicantDto;
//import com.mindware.workflow.ui.backend.entity.dto.CreditRequestApplicantDtoT;
import com.mindware.workflow.ui.backend.util.HeaderJwt;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CreditRequestApplicantDtoRestTemplate {
    private static RestTemplate restTemplate;

    public CreditRequestApplicantDtoRestTemplate(){
        restTemplate = new RestTemplate();
    }

    private List<CreditRequestApplicantDto> creditRequestApplicantDtoList = new ArrayList<>();

    public List<CreditRequestApplicantDto> getAll(){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/getAll";
        HttpEntity<CreditRequestApplicantDto[]> entity = new HttpEntity<>(HeaderJwt.getHeader());
        ResponseEntity<CreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequestApplicantDto[].class);
        CreditRequestApplicantDto[] creditRequestApplicantDtos = response.getBody();
        List<CreditRequestApplicantDto> list = new ArrayList<>();
        creditRequestApplicantDtoList = Arrays.asList(creditRequestApplicantDtos);
        for(CreditRequestApplicantDto c : creditRequestApplicantDtoList){
            CreditRequestApplicantDto creditRequestApplicantDto = new CreditRequestApplicantDto();
            creditRequestApplicantDto.setCurrency(c.getCurrency());
            creditRequestApplicantDto.setFirstName(c.getFirstName());
            creditRequestApplicantDto.setLastName(c.getLastName());
            creditRequestApplicantDto.setId(c.getId());
            creditRequestApplicantDto.setLoginUser(c.getLoginUser());
            creditRequestApplicantDto.setNumberRequest(c.getNumberRequest());
            creditRequestApplicantDto.setAmount(c.getAmount());
            creditRequestApplicantDto.setRequestDate(c.getRequestDate());
            creditRequestApplicantDto.setState( CreditRequestApplicantDto.State.valueOf(c.getState().getName()));
            creditRequestApplicantDto.setNumberApplicant(c.getNumberApplicant());
            creditRequestApplicantDto.setIdCreditRequest(c.getIdCreditRequest());
            creditRequestApplicantDto.setIdApplicant(c.getIdApplicant());
            creditRequestApplicantDto.setTypeRelation(c.getTypeRelation());


            list.add(creditRequestApplicantDto);
        }

        return list;
    }

    public List<CreditRequestApplicantDto> getByIdUserRegister(String loginUser){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/getByUser";
        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("login-user",loginUser);
        HttpEntity<CreditRequestApplicantDto[]> entity = new HttpEntity<>(headers);
        ResponseEntity<CreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequestApplicantDto[].class);
        creditRequestApplicantDtoList = Arrays.asList(response.getBody());
        return creditRequestApplicantDtoList;
    }

    public List<CreditRequestApplicantDto> getByLoginNumberRequest(String loginUser, Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/getByLoginNumberRequest";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("loginuser",loginUser);
        headers.add("numberrequest", numberRequest.toString());
        HttpEntity<CreditRequestApplicantDto> entity = new HttpEntity<>(headers);

        ResponseEntity<CreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequestApplicantDto[].class);

        return Arrays.asList(response.getBody());
    }

    public List<CreditRequestApplicantDto> getByNumberRequest(Integer numberRequest){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/getByNumberRequest";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("numberrequest", numberRequest.toString());
        HttpEntity<CreditRequestApplicantDto> entity = new HttpEntity<>(headers);

        ResponseEntity<CreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequestApplicantDto[].class);

        return Arrays.asList(response.getBody());
    }


    public List<CreditRequestApplicantDto> getByLoginUserTypeRelation(String loginUser, String typeRelation){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/getByLoginTypeRelation";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("loginuser",loginUser);
        headers.add("typerelation", typeRelation);
        HttpEntity<CreditRequestApplicantDto> entity = new HttpEntity<>(headers);

        ResponseEntity<CreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequestApplicantDto[].class);

        return Arrays.asList(response.getBody());
    }

    public List<CreditRequestApplicantDto> getAllByCity(String cityOffice){
        final String uri = "http://localhost:8080/rest/v1/creditrequestapplicant/getAllByCity";

        HttpHeaders headers = HeaderJwt.getHeader();
        headers.add("city-office",cityOffice);

        HttpEntity<CreditRequestApplicantDto> entity = new HttpEntity<>(headers);

        ResponseEntity<CreditRequestApplicantDto[]> response = restTemplate.exchange(uri, HttpMethod.GET,entity,CreditRequestApplicantDto[].class);

        return Arrays.asList(response.getBody());
    }
}

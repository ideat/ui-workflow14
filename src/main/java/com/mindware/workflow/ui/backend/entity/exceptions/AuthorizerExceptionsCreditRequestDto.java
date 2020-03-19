package com.mindware.workflow.ui.backend.entity.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class AuthorizerExceptionsCreditRequestDto {
    private Integer numberRequest;
    private Integer numberApplicant;
    private Double amount;
    private String currency;
    private String firstName;
    private String lastName;
    private String stateCreditRequest;
    private Integer totalExceptionsProposal;
    private Integer totalExceptionsRejected;
    private Integer totalExceptionsApproved;
    private String city;
    private String loginUser;

    public String getFullName(){
        return Optional.ofNullable(this.firstName).orElse("") +" "
                +Optional.ofNullable(this.lastName).orElse("");
    }

    public String getInitials(){
        List<String> names = new LinkedList<String>(Arrays.asList(getFullName().split(" ")));
        names.removeIf(item -> "".equals(item));
        if (names.size() == 1){
            return names.get(0).substring(0,1);
        }else{
            return names.get(0).substring(0,1)+names.get(1).substring(0,1);
        }
    }
}

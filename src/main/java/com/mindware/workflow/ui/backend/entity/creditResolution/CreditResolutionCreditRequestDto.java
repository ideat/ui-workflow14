package com.mindware.workflow.ui.backend.entity.creditResolution;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class CreditResolutionCreditRequestDto {
    private UUID idCreditRequestApplicant;
    private Integer numberRequest;
    private String typeCredit;
    private String firstName;
    private String secondName;
    private String lastName;
    private String motherLastName;
    private String state;
    private Double amount;
    private String currency;
    private String loginUser;
    private Integer numberApplicant;
    private String city;
    private String nameUser;
    private Integer numberApplicantSpouse;

    public String getFullName(){
        return  Optional.ofNullable(this.firstName).orElse("") + " "
                +Optional.ofNullable(this.secondName).orElse("")+ " "
                +Optional.ofNullable(this.lastName).orElse("")+" "
                +Optional.ofNullable(this.motherLastName).orElse("");

    }

    public String getInitials(){
        List<String>names = new LinkedList<String>(Arrays.asList(getFullName().split(" ")));
        names.removeIf(item -> "".equals(item));
        if (names.size() == 1){
            return names.get(0).substring(0,1);
        }else{
            return names.get(0).substring(0,1)+names.get(1).substring(0,1);
        }
    }
}

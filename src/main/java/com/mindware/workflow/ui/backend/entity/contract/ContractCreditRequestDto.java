package com.mindware.workflow.ui.backend.entity.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractCreditRequestDto {
    private String firstName;
    private String lastName;
    private Integer numberApplicant;
    private Integer numberRequest;
    private String nameOfficer;
    private String city;
    private String nameOffice;
    private String pathContract;

    public String getFullName(){
        return firstName + " " + lastName;
    }
    public String getInitials() {
        return (firstName.substring(0, 1) + lastName.substring(0, 1))
                .toUpperCase();
    }
}

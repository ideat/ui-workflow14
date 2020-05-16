package com.mindware.workflow.ui.backend.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreditRequestCompanySizeIndicatorDto {
    private Integer numberRequest;
    private Double amount;
    private String currency;
    private LocalDate requestDate;
    private String firstName;
    private String lastName;
    private String typeCredit;
    private String typeGuarantee;
    private Double indicator;
    private String companySizeIndicator;
    private String loginUser;
    private String city;
    private String nameOfficer;
    private String nameOffice;
    private String indicatorClassification;

    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }
}

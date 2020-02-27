package com.mindware.workflow.ui.backend.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreditRequestApplicantDtoT {
    private String numberRequest;
    private Double amount;
    private String currency;
    private LocalDate requestDate;
    private String state;
    private String firstName;
    private String lastName;
    private UUID id;
    private UUID idApplicant;
    private UUID idCreditRequest;
    private String loginUser;
    private Integer numberApplicant;
    private String typeRelation;
}

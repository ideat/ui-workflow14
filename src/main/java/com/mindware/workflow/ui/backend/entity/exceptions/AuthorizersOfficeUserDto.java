package com.mindware.workflow.ui.backend.entity.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizersOfficeUserDto {
    private String nameAuthorizer;
    private String stateAuthorizer;
    private String loginAuthorizer;
    private Double maximumAmountBs;
    private Double minimumAmountBs;
    private Double maximumAmountSus;
    private Double minimumAmountSus;
    private String scope;
    private String city;
    private String internalCodeOffice;
    private String typeOffice;
    private String email;

}

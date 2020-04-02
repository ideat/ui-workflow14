package com.mindware.workflow.ui.backend.entity.comercial.client;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
public class Client {
    private UUID id;

    private String names;

    private String lastNames;

    private String idCard;

    private String extension;

    private LocalDate registerDate;

    private String typePerson;

    private String typeClient;

    private String size;

    private String caedec;

    private String cellPhone;

    private String homePhone;

    private String email;

    private String addressHome;

    private String addressWork;

    private String loginUser;

    public String getFullName(){
        return this.names + " " + this.lastNames;
    }

    public String getIdCardComplete(){
        return this.idCard+this.extension;
    }

    public String getInitials(){
        return (names.substring(0,1)+lastNames.substring(0,1)).toUpperCase();
    }
}

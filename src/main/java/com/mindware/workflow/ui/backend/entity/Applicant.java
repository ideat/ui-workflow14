package com.mindware.workflow.ui.backend.entity;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class Applicant {

    private UUID id;

    private Integer numberApplicant;

    @NotNull(message = "'Primer nombre' no puede ser omitido")
    @NotBlank(message = "'Primer nombre' no puede ser omitido")
    @Size(min=2,max=60, message = "'nombres' debe tener entre 2 y 60 letras")
    @Pattern(regexp = "^[a-zA-Z\\u00F1\\u00D1 ]+$",message = "'Primer nombre' solo se permiten letras a-z , A-Z")
    private String firstName;

    @Pattern(regexp = "^[a-zA-Z\\u00F1\\u00D1 ]+$",message = "'Segundo nombre' solo se permiten letras a-z , A-Z")
    private String secondName;

    @Pattern(regexp = "^[a-zA-Z\\u00F1\\u00D1 ]+$",message = "'Apellido de casada' solo se permiten letras a-z , A-Z")
    private String marriedLastName;

    @Pattern(regexp = "^[a-zA-Z\\u00F1\\u00D1 ]+$",message = "'Apellido paterno' solo se permiten letras a-z , A-Z")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z\\u00F1\\u00D1 ]+$",message = "'Apellido materno' solo se permiten letras a-z , A-Z")
    private String motherLastName;

    @NotNull(message = "'Direccion domicilio', no puede omitirse")
    private String homeaddress;

    @NotNull(message = "'Carnet de Identidad' no puede ser omitido")
    @Pattern(regexp="\\d{5,10}(-[a-zA-Z_0-9]*)*",message = "'Carnet de Identidad' debe ser una secuencia de entre 5 y 10 dígitos, opcionalmente un caracter seguido de hasta 3 digitos " )
    @NotBlank(message = "'Carnet de Identidad' no puede ser omitido")
    @Size(min = 5, max = 15, message = "'Carnet de Identidad' debe tener entre 5 y 15 caracteres")
    private String idCard;

    @NotNull(message = "'Lugar expedicion carnet' no puede ser omitido")
    private String idCardExpedition;

    private LocalDate dateExpirationIdCard;

    @NotNull(message = "'Estado civil' no puede ser omitido")
    private String civilStatus;

    private int dependentNumber;

    @NotNull(message = "'Fecha de nacimiento' no debe ser omitida")
    @Past(message = "'Fecha de nacimiento' no debe ser en el futuro")
    private LocalDate birthdate;

    @NotNull(message = "'Genero' no puede ser omitido")
    private String gender;

    private String profession;

    private String nationality;

    @NotNull(message = "'CAEDEC ocupacion' no puede ser omitido")
    private String caedec;

    @NotNull(message = "'Fecha de registro' no puede ser omitida")
    private LocalDate registerDate;

    private String cellphone;

    private String homephone;

    private String workphone;

    private String workcellphone;

    private String workaddress;

    private String nameCompanyWork;

    private String position;

    private Integer workingtime;

    private String nit;

    private int idOffice;

    private String loginUser;

    private Integer numberApplicantSpouse;

    private String typePerson;

    private String companyData;

    private String city;

    private String block;

    private String typeHome;

    private String province;

    private LocalDate customerFrom;

    private String savingAccount;

    public String getIdCardComplet(){
        return this.idCard+this.idCardExpedition;
    }

    public String getFullName(){
        return Optional.ofNullable(this.lastName).orElse("")+" "
                +Optional.ofNullable(this.motherLastName).orElse("") + " "
                +Optional.ofNullable(this.secondName).orElse("")+ " "
                +Optional.ofNullable(this.firstName).orElse("");
    }
}

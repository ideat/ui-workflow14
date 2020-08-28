package com.mindware.workflow.ui.backend.entity.users;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class Users {

    private UUID id;

    @NotNull(message = "'idUsuario' no puede ser omitido")
    @NotBlank(message = "'idUsuario' no puede estar en blanco")
    @Size(min=4, max = 50, message = "'idUsuario' debe tener entre 4 y 50 letras")
    private String login;

    @NotNull(message = "'correoElectronico' no puede ser omitido")
    @Email(message = "'correoElectronico' no valido")
    private String email;

    private String password;

    private LocalDate dateUpdatePassword;

    private String names;

    private String lastNames;

    private int numDaysValidity;

    private String state;

    private String rol;

    @NotNull(message = "Oficina es requerida")
    private Integer codeOffice;

    private LocalDate dateCreation;

    private String position;

    private String supervisor;

    private String scope;

    public String getInitials(){
        return (names.substring(0,1)+lastNames.substring(0,1).toUpperCase());
    }

    public String getFullName(){
        return names + " " + lastNames;
    }





}

package com.mindware.workflow.ui.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Office {
    private UUID id;

    private UUID idRoot;

    @NotNull()
    private Integer internalCode;

    @NotNull(message = "'Nombre oficina' no puede ser omitido")
    private String name;

    @NotNull(message = "'Direccion oficina' no puede ser omitida")
    private String address;

    @NotNull(message = "'Telefono oficina' no puede ser omitido")
    private String phone;

    @NotNull(message = "'Ciudad oficina' no puede ser omitida")
    private String city;

    @NotNull(message = "'Provincia oficina' no puede ser omitida")
    private String province;

    @NotNull(message = "'Tipo de oficina' no puede ser omitido")
    private String typeOffice;

    private String signatorie;


    public boolean isNew(){
        return getId()==null;
    }
    public String getInitials(){
        String[] names = name.split(" ");
        if (names.length == 1){
            return names[0].substring(0,1);
        }else{
            return names[0].substring(0,1)+names[1].substring(0,1);
        }
    }

    public String getInitialsLocation(){
        return city.substring(0,1)+province.substring(0,1);
    }
}

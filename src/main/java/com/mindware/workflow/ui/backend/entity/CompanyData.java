package com.mindware.workflow.ui.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CompanyData {
    @JsonProperty("address")
    private String addressCompany;

    private String building;

    private String office;

    @JsonProperty("city")
    private String cityCompany;

    @JsonProperty("province")
    private String provinceCompany;

    @JsonProperty("block")
    private String blockCompany;

    @JsonProperty("phones")
    private String phonesCompany;

    @JsonProperty("email")
    private String emailCompany;

    private String comercialNumber;

    private String societyType;

    private String initials;

    private Integer antiquityArea;

    private Integer numberEmployees;

    private String webpage;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate constitutionDate;

}

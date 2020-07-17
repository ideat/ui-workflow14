package com.mindware.workflow.ui.backend.entity.creditResolution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreditResolution {
    private UUID id;

    private Integer numberRequest;

    private Double reciprocity;

    private String sector;

    private String item; //rubro

    private String creditObject; //objeto de la operacion

    private String conclusion;

    private String exceptions;// definir campos, json

    private String typeResolution;

    private String applicantRating; //calificacion

    private String amortizationDescription;

    private String directIndirectDebts; //deudas indirectas , definir campos json, se ingresa a mano

    private String numberDisbursements; //Numero desembolsos;

//    private String directDebts; //inicialmente vienen de los pasivos, se debe permitir ingresar datos
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate creationDate;

}

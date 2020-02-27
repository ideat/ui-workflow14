package com.mindware.workflow.ui.backend.entity.legal;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class LegalInformation {
    private UUID id;

    private Integer numberRequest;

    private String registration; //matricula

    private String registrationSeat; //json

    private String propertyType; //tipo inmueble

    private String location;

    private Double surface; //superficie

    private String north;

    private String south;

    private String east;

    private String west;

    private String owners;//json

    private String typeTitle; //tipo

    private String publicDeed;;//escritura publica

    private LocalDate dateDeed; //fecha escritura

    private String givenBy; //otorgado por

    private String documentsSubmitted;//json

    private String dataDocument; //json

//    private String observations; //json
//
//    private String missingDocumentation;//json
//
//    private String contractRequirements;//json
//
//    private String conclusion;//json
//
//    private String clarifications;//json

    private String details;//json

    private LocalDate creationDate;

    private String reportNumber;

    private String createdBy;
}

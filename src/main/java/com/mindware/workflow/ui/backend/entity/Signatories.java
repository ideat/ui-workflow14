package com.mindware.workflow.ui.backend.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Signatories {
    private UUID id;

    private String name;

    private String identifyCard;

    private String position;

    private String state;

//    private String numPower;

    private String numNotary;

    private String nameNotary;

    private String judicialDistrict;

    private String numTestimony;

    private String dateTestimony;

    private String priority;
}

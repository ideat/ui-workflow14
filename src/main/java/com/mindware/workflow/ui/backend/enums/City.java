package com.mindware.workflow.ui.backend.enums;

import lombok.Getter;

@Getter
public enum City {
    CBBA("COCHABAMBA","CB"),
    LAPAZ("LA PAZ", "LP"),
    CHUQUI("CHUQUISACA","CH"),
    ORURO("ORURO","OR"),
    TRJ("TARIJA","TJ"),
    PANDO("PANDO", "PD"),
    POTOSI("POTOSI","PO"),
    BENI("BENI","BE"),
    SANTA("SANTA CRUZ","SC");

    private String nameCity;
    private String abre;

    private City(String nameCity, String abre){
        this.nameCity = nameCity;
        this.abre = abre;
    }


}

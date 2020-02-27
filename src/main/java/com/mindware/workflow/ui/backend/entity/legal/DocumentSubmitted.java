package com.mindware.workflow.ui.backend.entity.legal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentSubmitted {

    private Integer firstYear; //gestio

    private String originalPhotocopyFirstYear;

    private Integer secondYear;

    private String originalPhotocopySecondYear;

    private Integer thirdYear;

    private String originalPhotocopyThirdYear;
}

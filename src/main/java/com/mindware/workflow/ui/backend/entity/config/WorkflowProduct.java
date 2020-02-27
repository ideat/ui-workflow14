package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WorkflowProduct {
    private UUID id;

    private String codeProductCredit;

    private String requestStage; //json

    private Integer totalHours;
}

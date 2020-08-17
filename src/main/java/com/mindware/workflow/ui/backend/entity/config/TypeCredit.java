package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TypeCredit {
    private UUID id;

    private String description;

    private String productTypeCredit; //json

    private String externalCode;

    private String objectCredit; //json
}

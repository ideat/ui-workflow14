package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductTypeCredit {
    private UUID id;

    private Integer externalCode;

    private String description;

}

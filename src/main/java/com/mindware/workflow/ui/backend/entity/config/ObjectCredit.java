package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ObjectCredit {
    private UUID id;

    private String description;

    private Integer externalCode;

    private String code;
}

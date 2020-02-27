package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TemplateForm {
    private UUID id;

    private String name;

    private String fieldsStructure;

    private String category;


}

package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldsStructure {

    private String componentName;

    private String componentLabel;

    private boolean visible;

    private Integer position;

    private boolean required;

    private String values;
}

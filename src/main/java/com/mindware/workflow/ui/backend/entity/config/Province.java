package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Province {
    private UUID id;
    private String name;
    private Integer externalCode;

}

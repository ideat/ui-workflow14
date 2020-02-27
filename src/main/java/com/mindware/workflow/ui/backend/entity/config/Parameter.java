package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
public class Parameter {
    private UUID id;

    @NotNull(message = "Categoria no puede ser omitida")
    private String category;

    private String value;

    private String description;



}

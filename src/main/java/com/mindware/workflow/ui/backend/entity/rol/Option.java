package com.mindware.workflow.ui.backend.entity.rol;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Option {
    private UUID id;

    private String name;

    private boolean assigned;

    private boolean write;

    private boolean read;

    private String description;

}

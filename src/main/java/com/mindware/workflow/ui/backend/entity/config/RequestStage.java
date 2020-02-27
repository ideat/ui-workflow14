package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class RequestStage {
    private UUID id;

    private String stage;

    private boolean active;

    private Integer position;

    private Integer hours;

    private String rols;

    private String states;//json
}

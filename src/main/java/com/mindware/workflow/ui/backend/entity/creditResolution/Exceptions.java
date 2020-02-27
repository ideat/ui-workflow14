package com.mindware.workflow.ui.backend.entity.creditResolution;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Exceptions {
    private UUID id;

    private String politicalNumber;

    private String description;
}

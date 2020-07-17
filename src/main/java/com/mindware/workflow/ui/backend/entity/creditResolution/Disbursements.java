package com.mindware.workflow.ui.backend.entity.creditResolution;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Disbursements {
    private UUID id;

    private String description;

    private Double amount;

    private String conditions;
}

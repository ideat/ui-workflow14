package com.mindware.workflow.ui.backend.entity.legal;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GenericItem {
    //Observations, Missing documents, Contract requiriment, Clarifications
    private UUID id;

    private String description;

    private String typeItem;
}

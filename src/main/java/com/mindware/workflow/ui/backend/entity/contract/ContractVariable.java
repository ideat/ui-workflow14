package com.mindware.workflow.ui.backend.entity.contract;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ContractVariable {
    private UUID id;

    private String typeVariable;

    private String name;

    private String variable;

}

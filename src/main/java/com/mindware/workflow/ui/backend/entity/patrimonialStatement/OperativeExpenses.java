package com.mindware.workflow.ui.backend.entity.patrimonialStatement;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class OperativeExpenses {
    private UUID id;

    private String expense;

    private Double amount;
}

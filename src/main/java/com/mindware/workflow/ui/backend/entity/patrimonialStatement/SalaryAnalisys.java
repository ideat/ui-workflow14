package com.mindware.workflow.ui.backend.entity.patrimonialStatement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalaryAnalisys {
    private String typeClient;
    private String numberMonth;
    private String description;
    private Double amount;
    private Integer priority;

}

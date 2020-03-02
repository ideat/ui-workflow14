package com.mindware.workflow.ui.backend.entity.cashFlow;

import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Getter
@Setter
public class CashFlow {

    private UUID id;

    private Integer numberRequest;

    private String items;

    private String description;

}

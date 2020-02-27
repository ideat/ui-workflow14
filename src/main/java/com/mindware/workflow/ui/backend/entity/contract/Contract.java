package com.mindware.workflow.ui.backend.entity.contract;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class Contract {
    private UUID id;

    private Integer numberRequest;

    private LocalDate dateContract;

    private String fileName;

    private String description;

    private String pathTemplate;
}

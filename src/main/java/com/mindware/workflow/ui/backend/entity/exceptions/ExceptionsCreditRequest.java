package com.mindware.workflow.ui.backend.entity.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ExceptionsCreditRequest {
    private UUID id;

    private String codeException;

    private Integer numberRequest;

    private String state;

    private LocalDate register;

    private String justification;

    private String statusReview;
}

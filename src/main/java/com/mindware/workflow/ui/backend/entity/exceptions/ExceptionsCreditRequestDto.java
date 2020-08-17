package com.mindware.workflow.ui.backend.entity.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ExceptionsCreditRequestDto {
    private UUID idExceptions;
    private Integer numberRequest;
    private String exceptionDetail;
    private String internalCode;
    private String stateException;
    private Integer limitTime;
    private LocalDate register;
    private String typeException;
    private String riskType;
}

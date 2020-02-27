package com.mindware.workflow.ui.backend.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomError {
    @JsonProperty
    private Integer code;
    @JsonProperty
    private String message;

    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}

package com.mindware.workflow.ui.backend.entity.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Authorizer {
    private UUID id;

    private String loginUser;

    private Double maximumAmountBs;

    private Double minimumAmountBs;

    private Double maximumAmountSus;

    private Double minimumAmountSus;

    private String scope;

    private String state;
}

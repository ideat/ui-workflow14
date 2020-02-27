package com.mindware.workflow.ui.backend.entity.creditRequest;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class StateHistory {
    private UUID id;

    private Instant dateState;

    private String state;

    private String location;
}

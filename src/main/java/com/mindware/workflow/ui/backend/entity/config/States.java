package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class States {
    private String state;

    private boolean active;

    private boolean finished;

    private boolean goForward;

    private boolean goBackward;

    private boolean initState;

    private boolean finishState;
}

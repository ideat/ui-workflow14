package com.mindware.workflow.ui.backend.entity.config;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CityProvince {
    private UUID id;

    private String city;

    private String provinces;

    private Integer externalCode;
}

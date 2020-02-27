package com.mindware.workflow.ui.backend.entity.creditRequest;

import com.mindware.workflow.ui.backend.enums.TypeLinkUp;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/*
Binding economic, enterprise, reference persons, guarantor loan
 */
@Getter
@Setter
public class LinkUp {
    private UUID id;

    private String firstField;

    private String seconfField;

    private String thirdField;

    private String fouthField;

    private String fifthField;

    private TypeLinkUp typeLinkUp;



}

package com.mindware.workflow.ui.backend.entity.patrimonialStatement;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SalesProjection {
    private UUID id;

    private String period;

    private String typeSale;

    private String categorySale;

    private Double lowSale;

    private Double hightSale;

    private Double averageSale;

}

package com.mindware.workflow.ui.backend.entity.patrimonialStatement;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CostProduct {
    private UUID id;

    private String product;

    private String supplies;

    private Integer quantity;

    private String unity;

    private Double priceUnity;

    private Integer factorConversion;

    private Double endQuantity;

    private Double totalCostUnit;

    private Double priceSale;
}

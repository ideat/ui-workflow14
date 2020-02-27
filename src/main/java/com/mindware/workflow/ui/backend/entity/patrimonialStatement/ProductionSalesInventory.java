package com.mindware.workflow.ui.backend.entity.patrimonialStatement;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductionSalesInventory {
    private UUID id;

    private String typeInventory;

    private Integer quantity;

    private String unity;

    private String rawMaterial;

    private Double priceCost;

    private Double priceSale;

    private Double mb;

    private Double advancePercentage;

    private Double inventoryValueMb;

    private Double inventoryValueFinished;

    private Double factor;
}

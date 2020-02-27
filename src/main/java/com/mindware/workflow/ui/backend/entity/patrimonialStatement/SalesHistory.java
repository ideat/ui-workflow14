package com.mindware.workflow.ui.backend.entity.patrimonialStatement;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SalesHistory {
    private UUID id;

    private String concept;

    private String numberMonth;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate daySale;

    @NotNull(message = "Monto no puede ser vacio")
    @PositiveOrZero(message = "El monto no puede ser negativo")
    private Double amount;

}

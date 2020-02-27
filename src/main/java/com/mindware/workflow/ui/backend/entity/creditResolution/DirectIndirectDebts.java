package com.mindware.workflow.ui.backend.entity.creditResolution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class DirectIndirectDebts {
    private UUID id;

    private String entity;

    private Double amount;

    private String state;

    private String rating;

    private String guarantee;

    private Double rate;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate finalExpiration;

    private String typeDebts;
}

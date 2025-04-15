package com.itau.pixms.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record AmountDto(@JsonProperty("original")
                        @Positive
                        @NotNull
                        @Digits(integer = 10, fraction = 2)
                        BigDecimal original,
                        @JsonProperty("modalidadeAlteracao")
                        int changeMode) {}

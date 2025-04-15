package com.itau.pixsub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record AmountDto(@JsonProperty("original")
                        BigDecimal original,
                        @JsonProperty("modalidadeAlteracao")
                        int changeMode) {}
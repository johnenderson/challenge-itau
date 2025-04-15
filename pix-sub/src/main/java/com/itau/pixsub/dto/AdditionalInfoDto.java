package com.itau.pixsub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AdditionalInfoDto(@JsonProperty("nome")
                                String name,
                                @JsonProperty("valor")
                                String value) {}
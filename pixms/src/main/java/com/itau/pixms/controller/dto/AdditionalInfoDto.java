package com.itau.pixms.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

public record AdditionalInfoDto(@JsonProperty("nome")
                                @Size(max = 50)
                                String name,
                                @JsonProperty("valor")
                                @Size(max = 200)
                                String value) {}

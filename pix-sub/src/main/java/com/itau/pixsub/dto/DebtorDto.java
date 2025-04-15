package com.itau.pixsub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DebtorDto(@JsonProperty("cpf")
                        String cpf,
                        @JsonProperty("cnpj")
                        String cnpj,
                        @JsonProperty("nome")
                        String name) {}
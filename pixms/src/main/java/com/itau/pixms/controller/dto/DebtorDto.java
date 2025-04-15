package com.itau.pixms.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;

public record DebtorDto(@CPF
                        @JsonProperty("cpf")
                        String cpf,
                        @CNPJ
                        @JsonProperty("cnpj")
                        String cnpj,
                        @JsonProperty("nome")
                        @Size(max = 200)
                        String name) {}

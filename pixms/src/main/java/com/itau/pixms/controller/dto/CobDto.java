package com.itau.pixms.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CobDto(@JsonProperty("calendario")
                     CobCalendarDto cobCalendarDto,
                     @JsonProperty("txid")
                     UUID txId,
                     @JsonProperty("revisao")
                     int revision,
                     @JsonProperty("status")
                     StatusOperation statusOperation,
                     @JsonProperty("devedor")
                     DebtorDto debtorDto,
                     @JsonProperty("valor")
                     AmountDto amountDto,
                     @JsonProperty("chave")
                     @Size(max = 77)
                     @NotBlank
                     String key,
                     @Size(max = 140)
                     @JsonProperty("solicitacaoPagador")
                     String payerRequest,
                     @JsonProperty("infoAdicionais")
                     List<AdditionalInfoDto> additionalInfoDto) {}
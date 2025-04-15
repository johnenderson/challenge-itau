package com.itau.pixms.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itau.pixms.type.StatusOperationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CobDto(@JsonProperty("calendario")
                     CobCalendarDto cobCalendarDto,
                     @JsonProperty("txid")
                     String txId,
                     @JsonProperty("revisao")
                     int revision,
                     @JsonProperty("status")
                     StatusOperationType statusOperationType,
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
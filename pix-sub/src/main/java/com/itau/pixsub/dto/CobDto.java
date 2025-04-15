package com.itau.pixsub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.itau.pixsub.type.StatusOperationType;

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
                     String key,
                     @JsonProperty("solicitacaoPagador")
                     String payerRequest,
                     @JsonProperty("infoAdicionais")
                     List<AdditionalInfoDto> additionalInfoDto) {}
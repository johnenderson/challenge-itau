package com.itau.pixms.validation.impl;

import com.itau.pixms.validation.ItemsForValidation;
import com.itau.pixms.validation.PixPayloadRules;

import java.util.Objects;

public class DebtorRules implements PixPayloadRules {

    @Override
    public void valid(ItemsForValidation itemsForValidation) {
        var debtorDto = itemsForValidation.debtorDto();

        if (Objects.nonNull(debtorDto.cpf()) && Objects.nonNull(debtorDto.cnpj())) {
            throw new IllegalArgumentException("Não é permitido preencher CPF e CNPJ simultaneamente");
        }
    }
}

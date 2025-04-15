package com.itau.pixms.validation.impl;

import com.itau.pixms.exception.InvalidDebtorException;
import com.itau.pixms.validation.ItemsForValidation;
import com.itau.pixms.validation.PixPayloadRules;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DebtorRules implements PixPayloadRules {

    @Override
    public void valid(ItemsForValidation itemsForValidation) {
        var debtorDto = itemsForValidation.debtorDto();

        if (Objects.nonNull(debtorDto.cpf()) && Objects.nonNull(debtorDto.cnpj())) {
            throw new InvalidDebtorException("Não é permitido preencher CPF e CNPJ simultaneamente");
        }
    }
}

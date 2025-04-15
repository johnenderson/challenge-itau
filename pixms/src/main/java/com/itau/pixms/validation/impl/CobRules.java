package com.itau.pixms.validation.impl;

import com.itau.pixms.exception.InvalidPixKeyException;
import com.itau.pixms.type.PixKeyType;
import com.itau.pixms.validation.ItemsForValidation;
import com.itau.pixms.validation.PixPayloadRules;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class CobRules implements PixPayloadRules {

    @Override
    public void valid(ItemsForValidation itemsForValidation) {
        var cobDto = itemsForValidation.cobDto();

        String pixKey = cobDto.key();

        PixKeyType keyType = PixKeyType.detectType(pixKey);
        if (isNull(keyType)) {
            throw new InvalidPixKeyException("Chave Pix inválida: formato não reconhecido");
        }
    }
}

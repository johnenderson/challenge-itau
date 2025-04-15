package com.itau.pixms.mapper;

import com.itau.pixms.controller.dto.AdditionalInfoDto;
import com.itau.pixms.controller.dto.AmountDto;
import com.itau.pixms.controller.dto.CobCalendarDto;
import com.itau.pixms.controller.dto.CobDto;
import com.itau.pixms.controller.dto.DebtorDto;
import com.itau.pixms.infrastructure.entity.Cob;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CobMapper {

    public CobDto toDto(Cob cob) {
        if (cob == null) return null;

        return new CobDto(
                cob.getCalendar() != null
                        ? new CobCalendarDto(cob.getCalendar().createdAt(), cob.getCalendar().expiration())
                        : null,
                cob.getTxId(),
                cob.getRevision(),
                cob.getStatusOperation(),
                cob.getDebtor() != null
                        ? new DebtorDto(cob.getDebtor().cpf(), cob.getDebtor().cnpj(), cob.getDebtor().name())
                        : null,
                cob.getAmount() != null
                        ? new AmountDto(cob.getAmount().original(), cob.getAmount().changeMode())
                        : null,
                cob.getKey(),
                cob.getPayerRequest(),
                cob.getAdditionalInfo() != null
                        ? cob.getAdditionalInfo().stream()
                        .map(info -> new AdditionalInfoDto(info.name(), info.value()))
                        .toList()
                        : List.of()
        );
    }

    public Cob toEntity(CobDto dto) {
        return new Cob.Builder()
                .withTxId(dto.txId())
                .withKey(dto.key())
                .withRevision(dto.revision())
                .withStatusOperation(dto.statusOperationType())
                .withPayerRequest(dto.payerRequest())
                .withCalendar(
                        dto.cobCalendarDto().createdAt(),
                        dto.cobCalendarDto().expiration()
                )
                .withDebtor(
                        dto.debtorDto().cpf(),
                        dto.debtorDto().cnpj(),
                        dto.debtorDto().name()
                )
                .withAmount(
                        dto.amountDto().original(),
                        dto.amountDto().changeMode()
                )
                .build();
    }
}

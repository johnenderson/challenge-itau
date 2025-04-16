package com.itau.pixconfirm.service;

import com.itau.pixconfirm.dto.ConfirmationDto;
import com.itau.pixconfirm.infrastructure.repository.CobRepository;
import com.itau.pixconfirm.type.StatusOperationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PixConfirmService {

    private final CobRepository cobRepository;

    public PixConfirmService(CobRepository cobRepository) {
        this.cobRepository = cobRepository;
    }

    public void confirmPix(List<ConfirmationDto> eventDtos) {
        eventDtos.forEach(dto ->
                cobRepository.updateStatusOperationById(
                        dto.txId(),
                        StatusOperationType.CONCLUIDA
                )
        );
    }
}
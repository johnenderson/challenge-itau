package com.itau.pixconfirm.service;

import com.itau.pixconfirm.dto.ConfirmationDto;
import com.itau.pixconfirm.infrastructure.repository.CobRepository;
import com.itau.pixconfirm.type.StatusOperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
public class PixConfirmService {

    private static final Logger logger = LoggerFactory.getLogger(PixConfirmService.class);

    private static final String LOG_START_CONFIRMATION = "Starting confirmation of {} Pix transactions";
    private static final String LOG_CONFIRMATION_COMPLETED = "Pix transactions confirmation completed";
    private static final String LOG_INVALID_CONFIRMATION = "Invalid confirmation detected: {}";
    private static final String LOG_CONFIRMATION_SUCCESS = "Pix transaction successfully confirmed: {}";
    private static final String LOG_CONFIRMATION_ERROR = "Error confirming Pix transaction {}: {}";
    private static final String ERROR_NULL_CONFIRMATIONS = "Confirmations list cannot be null";
    private static final String ERROR_PROCESSING_CONFIRMATION = "Failed to process Pix confirmation: {}";

    private final CobRepository cobRepository;

    public PixConfirmService(CobRepository cobRepository) {
        this.cobRepository = cobRepository;
    }

    public void confirmPix(List<ConfirmationDto> confirmations) {
        Objects.requireNonNull(confirmations, ERROR_NULL_CONFIRMATIONS);

        logger.info(LOG_START_CONFIRMATION.replace("{}", String.valueOf(confirmations.size())));

        confirmations.stream()
                .filter(this::isValidConfirmation)
                .forEach(this::processConfirmation);

        logger.info(LOG_CONFIRMATION_COMPLETED);
    }

    private boolean isValidConfirmation(ConfirmationDto confirmation) {
        if (Objects.isNull(confirmation) || !StringUtils.hasText(confirmation.txId())) {
            String message = Objects.isNull(confirmation) ? "null confirmation" :
                    "confirmation with invalid txId: " + confirmation.txId();
            logger.error(LOG_INVALID_CONFIRMATION, message);
            return false;
        }
        return true;
    }

    private void processConfirmation(ConfirmationDto confirmation) {
        try {
            cobRepository.updateStatusOperationById(
                    confirmation.txId(),
                    StatusOperationType.CONCLUIDA
            );
            logger.debug(LOG_CONFIRMATION_SUCCESS.replace("{}", confirmation.txId()));
        } catch (Exception e) {
            logger.debug(LOG_CONFIRMATION_ERROR, confirmation.txId(), e.getMessage());
            throw new RuntimeException(ERROR_PROCESSING_CONFIRMATION.replace("{}", confirmation.txId()), e);
        }
    }
}
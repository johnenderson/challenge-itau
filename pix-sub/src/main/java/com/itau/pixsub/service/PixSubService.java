package com.itau.pixsub.service;

import com.itau.pixsub.client.SendPspPayment;
import com.itau.pixsub.client.dto.SendPspPaymentResponse;
import com.itau.pixsub.dto.CobDto;
import com.itau.pixsub.dto.ConfirmationDto;
import com.itau.pixsub.exception.PixChargeProcessingException;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PixSubService {

    private static final Logger logger = LoggerFactory.getLogger(PixSubService.class);

    private static final String PROCESSING_MESSAGES = "Processing {} messages";
    private static final String SENDING_CHARGE = "Sending charge with txId: {} to destination service";
    private static final String CHARGE_SENT_SUCCESSFULLY = "Charge sent successfully, sending confirmation message for txId: {}";
    private static final String CHARGE_RESPONSE_NOT_TRUE = "Charge response status is not true for txId: {}";
    private static final String ERROR_SENDING_CHARGE = "Error sending charge to destination service for txId {}: {}";
    private static final String ALL_MESSAGES_PROCESSED = "All messages processed successfully";
    private static final String ERROR_SENDING_CONFIRMATION = "Error sending confirmation message for txId {}: {}";
    private static final String CONFIRMATION_SENT = "Confirmation message sent successfully for txId {}. Message ID: {}";
    private static final String EXCEPTION_SENDING_CONFIRMATION = "Exception when trying to send confirmation for txId {}: {}";

    private final SendPspPayment sendPspPayment;
    private final AuthService authService;
    private final SqsTemplate sqsTemplate;

    public PixSubService(SendPspPayment sendPspPayment,
                         AuthService authService,
                         SqsTemplate sqsTemplate) {
        this.sendPspPayment = sendPspPayment;
        this.authService = authService;
        this.sqsTemplate = sqsTemplate;
    }

    public void processPix(List<CobDto> cobDtos) {
        if (cobDtos == null || cobDtos.isEmpty()) {
            logger.info("No messages to process");
            return;
        }

        logger.debug(PROCESSING_MESSAGES, cobDtos.size());
        String token = authService.getToken();

        for (CobDto cobDto : cobDtos) {
            processPixCharge(token, cobDto);
        }

        logger.debug(ALL_MESSAGES_PROCESSED);
    }

    private void processPixCharge(String token, CobDto cobDto) {
        try {
            String txId = cobDto.txId();
            logger.debug(SENDING_CHARGE, txId);

            SendPspPaymentResponse response = sendChargeToBacen(token, cobDto);

            if (isResponseSuccessful(response)) {
                logger.debug(CHARGE_SENT_SUCCESSFULLY, txId);
                sendConfirmationMessage(new ConfirmationDto(txId));
            } else {
                logger.warn(CHARGE_RESPONSE_NOT_TRUE, txId);
            }
        } catch (Exception e) {
            logger.error(ERROR_SENDING_CHARGE, cobDto.txId(), e.getMessage(), e);
            throw new PixChargeProcessingException("Failed to process PIX charge", e);
        }
    }

    private SendPspPaymentResponse sendChargeToBacen(String token, CobDto cobDto) {
        return sendPspPayment.sendToBacen(token, cobDto).getBody();
    }

    private boolean isResponseSuccessful(SendPspPaymentResponse response) {
        return Objects.nonNull(response) && Boolean.TRUE.equals(response.received());
    }

    private void sendConfirmationMessage(ConfirmationDto dto) {
        try {
            String txId = dto.txId();
            sqsTemplate.sendAsync(dto)
                    .whenComplete((result, ex) -> {
                        if (Objects.nonNull(ex)) {
                            logger.error(ERROR_SENDING_CONFIRMATION, txId, ex.getMessage(), ex);
                        } else {
                            logger.info(CONFIRMATION_SENT, txId, result.messageId());
                        }
                    });
        } catch (Exception e) {
            String txId = dto.txId();
            logger.error(EXCEPTION_SENDING_CONFIRMATION, txId, e.getMessage(), e);
        }
    }
}
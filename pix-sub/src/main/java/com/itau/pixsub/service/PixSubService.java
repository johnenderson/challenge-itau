package com.itau.pixsub.service;

import com.itau.pixsub.client.SendPspPayment;
import com.itau.pixsub.client.dto.SendPspPaymentResponse;
import com.itau.pixsub.dto.CobDto;
import com.itau.pixsub.dto.ConfirmationDto;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class PixSubService {

    private static final Logger logger = LoggerFactory.getLogger(PixSubService.class);

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
        logger.debug("Processing {} messages", cobDtos.size());
        var token = authService.getToken();

        for (CobDto cobDto : cobDtos) {
            try {
                logger.debug("Sending charge with txId: {} to destination service", cobDto.txId());
                SendPspPaymentResponse response = sendPspPayment.sendToBacen(token, cobDto).getBody();

                if (Objects.nonNull(response) && Boolean.TRUE.equals(response.received())) {
                    logger.debug("Charge sent successfully, sending confirmation message for txId: {}", cobDto.txId());

                    var txIdConfirm = new ConfirmationDto(
                            cobDto.txId()
                    );

                    sendConfirmationMessage(txIdConfirm);
                } else {
                    logger.warn("Charge response status is not true for txId: {}", cobDto.txId());
                }
            } catch (Exception e) {
                logger.error("Error sending charge to destination service: {}", e.getMessage(), e);
                throw e;
            }
        }

        logger.debug("All messages processed successfully");
    }

    private void sendConfirmationMessage(ConfirmationDto dto) {
        try {
            var future = sqsTemplate.sendAsync(dto);
            future.whenComplete((result, ex) -> {
                if (Objects.nonNull(ex)) {
                    logger.error("Error sending confirmation message for txId {}: {}", dto.txId(), ex.getMessage(), ex);
                } else {
                    logger.info("Confirmation message sent successfully for txId {}. Message ID: {}", dto.txId(), result.messageId());
                }
            });
        } catch (Exception e) {
            logger.error("Exception when trying to send confirmation for txId {}: {}", dto.txId(), e.getMessage(), e);
        }
    }
}

package com.itau.pixconfirm.listener;

import com.itau.pixconfirm.dto.ConfirmationDto;
import com.itau.pixconfirm.service.PixConfirmService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.BatchAcknowledgement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PixConfirmListener {

    private static final Logger logger = LoggerFactory.getLogger(PixConfirmListener.class);

    private final PixConfirmService pixConfirmService;

    public PixConfirmListener(PixConfirmService pixConfirmService) {
        this.pixConfirmService = pixConfirmService;
    }

    /**
     * Listener SQS que processa mensagens de eventos em lote. Utiliza confirmação manual para
     * garantir que todas as mensagens do lote sejam processadas corretamente antes de serem removidas
     * da fila.
     */
    @SqsListener(
            value = "${app.aws.confirm-queue-name}",
            acknowledgementMode = "MANUAL",
            messageVisibilitySeconds = "300")
    public void listen(List<Message<ConfirmationDto>> messages, BatchAcknowledgement<ConfirmationDto> batchAcknowledgement) {
        long startTime = System.currentTimeMillis();
        logger.debug("Received batch with {} messages", messages.size());

        if (messages.isEmpty()) {
            logger.debug("Empty message batch received, skipping processing");
            return;
        }

        try {
            List<ConfirmationDto> eventDtos = messages.stream().map(Message::getPayload)
                    .collect(Collectors.toList());

            pixConfirmService.confirmPix(eventDtos);

            batchAcknowledgement.acknowledgeAsync(messages);
            logger.debug("All {} messages acknowledged successfully", messages.size());

        } catch (Exception ex) {
            logger.error("Error processing batch: {}", ex.getMessage(), ex);
        }

        logger.debug("Batch processing time: {} ms", System.currentTimeMillis() - startTime);
    }
}

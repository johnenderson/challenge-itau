package com.itau.pixsub.listener;

import com.itau.pixsub.dto.CobDto;
import com.itau.pixsub.service.PixSubService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.listener.acknowledgement.BatchAcknowledgement;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class PixListener {

    private static final Logger logger = LoggerFactory.getLogger(PixListener.class);

    private final PixSubService pixSubService;

    public PixListener(PixSubService pixSubService) {
        this.pixSubService = pixSubService;
    }

    /**
     * Listener SQS que processa mensagens de eventos em lote. Utiliza confirmação manual para
     * garantir que todas as mensagens do lote sejam processadas corretamente antes de serem removidas
     * da fila.
     */
    @SqsListener(
            value = "${app.aws.send-queue-name}",
            acknowledgementMode = "MANUAL",
            messageVisibilitySeconds = "300")
    public void listen(List<Message<CobDto>> messages, BatchAcknowledgement<CobDto> batchAcknowledgement) {
        long startTime = System.currentTimeMillis();
        logger.debug("Received batch with {} messages", messages.size());

        if (messages.isEmpty()) {
            logger.debug("Empty message batch received, skipping processing");
            return;
        }

        try {
            List<CobDto> eventDtos = messages.stream().map(Message::getPayload)
                    .collect(Collectors.toList());

            pixSubService.processPix(eventDtos);

            batchAcknowledgement.acknowledgeAsync(messages);
            logger.debug("All {} messages acknowledged successfully", messages.size());

        } catch (Exception ex) {
            logger.error("Error processing batch: {}", ex.getMessage(), ex);
        }

        logger.debug("Batch processing time: {} ms", System.currentTimeMillis() - startTime);
    }
}
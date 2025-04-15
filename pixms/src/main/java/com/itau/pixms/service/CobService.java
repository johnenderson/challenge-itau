package com.itau.pixms.service;

import com.itau.pixms.client.FraudMarkersClient;
import com.itau.pixms.client.dto.FraudMarkersResponse;
import com.itau.pixms.client.dto.KeyRequest;
import com.itau.pixms.controller.dto.CobDto;
import com.itau.pixms.controller.dto.StatusOperation;
import com.itau.pixms.exception.FraudMarkerException;
import com.itau.pixms.infrastructure.entity.Cob;
import com.itau.pixms.infrastructure.repository.CobRepository;
import com.itau.pixms.validation.ItemsForValidation;
import com.itau.pixms.validation.PixPayloadRules;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CobService {

    private static final Logger logger = LoggerFactory.getLogger(CobService.class);

    private final AuthService authService;
    private final FraudMarkersClient fraudMarkersClient;
    private final List<PixPayloadRules> validationRules;
    private final CobRepository cobRepository;
    private final static int REVISION_PAYLOAD = 0;
    private final static int CHANGE_MODE = 0;
    private final static int EXPIRATION_DEFAULT_IN_SECONDS = 86400;
    private final static String MOCK_CNPJ = "12345678000195";
    private final static String MOCK_NAME = "EMPRESA DE SERVIÇOS SA";


    public CobService(AuthService authService,
                      FraudMarkersClient fraudMarkersClient,
                      List<PixPayloadRules> validationRules,
                      CobRepository cobRepository) {
        this.authService = authService;
        this.fraudMarkersClient = fraudMarkersClient;
        this.validationRules = validationRules;
        this.cobRepository = cobRepository;
    }

    public void createImmediateCharge(@Valid CobDto dto) {

        ItemsForValidation items = new ItemsForValidation(
                dto.debtorDto()
        );
        for (PixPayloadRules rule : validationRules) {
            rule.valid(items);
        }

        // 1. Autenticação na api para recuparar o token
        var token = authService.getToken();

        // 3. Verificar fraude
        checkForFraud(token, dto.key());

        // 4. Construir cobrança imediata e salvar transacao no DynamoDB
        persist(dto);

        //6. Enviar para o SQS
    }

    private void persist(CobDto dto) {
        UUID uuid = UUID.randomUUID();
        String txId = uuid.toString().replace("-", "");

        var entity = new Cob.Builder()
                .withTxId(txId)
                .withKey(dto.key())
                .withRevision(REVISION_PAYLOAD)
                .withStatusOperation(StatusOperation.ATIVA)
                .withPayerRequest(dto.payerRequest())
                .withCalendar(
                        LocalDateTime.now(ZoneOffset.UTC),
                        EXPIRATION_DEFAULT_IN_SECONDS
                )
                .withDebtor(
                        null,
                        MOCK_CNPJ,
                        MOCK_NAME
                )
                .withAmount(
                        dto.amountDto().original(),
                        CHANGE_MODE
                )
                .build();

        cobRepository.save(entity);
    }

    private void checkForFraud(String token, String key) {
        var request = new KeyRequest(key);
        var response = fraudMarkersClient.fraudCheck(token, request);

        if (response.getStatusCode().value() != HttpStatus.OK.value()) {
            logger.error("error while sending purchase request, " +
                            "status: {}, response: {}",
                    response.getStatusCode(), response.getBody());

        }

        boolean isFraudulent = Optional.of(response)
                .map(HttpEntity::getBody)
                .map(FraudMarkersResponse::fraudMarker)
                .orElse(false);

        if (isFraudulent) {
            throw new FraudMarkerException("This PIX key is marked as fraud, the transaction will not be completed");
        }
    }
}

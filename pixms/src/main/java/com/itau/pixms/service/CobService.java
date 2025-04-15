package com.itau.pixms.service;

import com.itau.pixms.client.FraudMarkersClient;
import com.itau.pixms.client.dto.FraudMarkersResponse;
import com.itau.pixms.client.dto.KeyRequest;
import com.itau.pixms.controller.dto.AmountDto;
import com.itau.pixms.controller.dto.CobCalendarDto;
import com.itau.pixms.controller.dto.CobDto;
import com.itau.pixms.controller.dto.DebtorDto;
import com.itau.pixms.mapper.CobMapper;
import com.itau.pixms.type.StatusOperationType;
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

import java.time.LocalDateTime;
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
    private final CobMapper cobMapper;
    private final static int REVISION_PAYLOAD = 0;
    private final static int CHANGE_MODE = 0;
    private final static int EXPIRATION_DEFAULT_IN_SECONDS = 86400;
    private final static String MOCK_CNPJ = "12345678000195";
    private final static String MOCK_NAME = "EMPRESA DE SERVIÇOS SA";


    public CobService(AuthService authService,
                      FraudMarkersClient fraudMarkersClient,
                      List<PixPayloadRules> validationRules,
                      CobRepository cobRepository,
                      CobMapper cobMapper) {
        this.authService = authService;
        this.fraudMarkersClient = fraudMarkersClient;
        this.validationRules = validationRules;
        this.cobRepository = cobRepository;
        this.cobMapper = cobMapper;
    }

    public CobDto buildRealTimePayment(@Valid CobDto inputDto) {
        CobDto dto = mock(inputDto);

        ItemsForValidation items = new ItemsForValidation(
                dto.debtorDto(), dto
        );
        for (PixPayloadRules rule : validationRules) {
            rule.valid(items);
        }

        // 1. Autenticação na api para recuperar o token
        var token = authService.getToken();

        // 3. Verificar fraude
        checkForFraud(token, dto.key());

        // 4. Salvar transacao
        var entity = persist(dto);

        return cobMapper.toDto(entity);

        //6. Enviar para o SQS
    }

    private Cob persist(CobDto dto) {
        Cob entity = cobMapper.toEntity(dto);
        return cobRepository.save(entity);
    }

    private CobDto mock(CobDto inputDto) {
        UUID uuid = UUID.randomUUID();
        String txId = uuid.toString().replace("-", "");

        DebtorDto debtorDto = new DebtorDto(null, MOCK_CNPJ, MOCK_NAME);
        AmountDto amountDto = inputDto.amountDto();

        CobCalendarDto calendarDto = new CobCalendarDto(
                LocalDateTime.now(ZoneOffset.UTC),
                EXPIRATION_DEFAULT_IN_SECONDS
        );

        return new CobDto(
                calendarDto,
                txId,
                REVISION_PAYLOAD,
                StatusOperationType.ATIVA,
                debtorDto,
                amountDto,
                inputDto.key(),
                inputDto.payerRequest(),
                inputDto.additionalInfoDto()
        );
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

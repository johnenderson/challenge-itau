package com.itau.pixms.validation.impl;

import com.itau.pixms.controller.dto.AdditionalInfoDto;
import com.itau.pixms.controller.dto.AmountDto;
import com.itau.pixms.controller.dto.CobCalendarDto;
import com.itau.pixms.controller.dto.CobDto;
import com.itau.pixms.controller.dto.DebtorDto;
import com.itau.pixms.exception.InvalidPixKeyException;
import com.itau.pixms.type.StatusOperationType;
import com.itau.pixms.validation.ItemsForValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CobRulesTest {

    @Mock
    private ItemsForValidation itemsForValidation;

    @InjectMocks
    private CobRules cobRules;

    private CobDto validCpfCobDto;
    private CobDto validEmailCobDto;
    private CobDto validPhoneCobDto;
    private CobDto validRandomKeyCobDto;
    private CobDto invalidCobDto;

    @BeforeEach
    void setUp() {
        this.buildDtos();
    }

    @Test
    void shouldPassValidationForValidCpfKey() {
        when(itemsForValidation.cobDto()).thenReturn(validCpfCobDto);

        assertDoesNotThrow(() -> cobRules.valid(itemsForValidation));
    }

    @Test
    void shouldPassValidationForValidEmailKey() {
        when(itemsForValidation.cobDto()).thenReturn(validEmailCobDto);

        assertDoesNotThrow(() -> cobRules.valid(itemsForValidation));
    }

    @Test
    void shouldPassValidationForValidPhoneKey() {
        when(itemsForValidation.cobDto()).thenReturn(validPhoneCobDto);

        assertDoesNotThrow(() -> cobRules.valid(itemsForValidation));
    }

    @Test
    void shouldPassValidationForValidRandomKey() {
        when(itemsForValidation.cobDto()).thenReturn(validRandomKeyCobDto);

        assertDoesNotThrow(() -> cobRules.valid(itemsForValidation));
    }

    @Test
    void shouldThrowExceptionForInvalidKey() {
        when(itemsForValidation.cobDto()).thenReturn(invalidCobDto);

        InvalidPixKeyException exception = assertThrows(InvalidPixKeyException.class,
                () -> cobRules.valid(itemsForValidation));
        assert(exception.getMessage().equals("Chave Pix inválida: formato não reconhecido"));
    }

    private void buildDtos() {
        CobCalendarDto cobCalendarDto = new CobCalendarDto(
                LocalDateTime.now(),
                3600
        );

        DebtorDto debtorDto = new DebtorDto(
                "12345678909",
                null,
                "João da Silva"
        );

        AmountDto amountDto = new AmountDto(
                new BigDecimal("100.00"),
                0
        );

        List<AdditionalInfoDto> additionalInfoDtos = new ArrayList<>();

        // Valid CPF key
        validCpfCobDto = new CobDto(
                cobCalendarDto,
                "TX123456",
                1,
                StatusOperationType.ATIVA,
                debtorDto,
                amountDto,
                "12345678909",
                "Pagamento de fatura",
                additionalInfoDtos
        );

        // Valid Email key
        validEmailCobDto = new CobDto(
                cobCalendarDto,
                "TX123457",
                1,
                StatusOperationType.ATIVA,
                debtorDto,
                amountDto,
                "teste@email.com",
                "Pagamento de fatura",
                additionalInfoDtos
        );

        // Valid Phone key
        validPhoneCobDto = new CobDto(
                cobCalendarDto,
                "TX123458",
                1,
                StatusOperationType.ATIVA,
                debtorDto,
                amountDto,
                "+5511999999999",
                "Pagamento de fatura",
                additionalInfoDtos
        );

        // Valid Random key
        validRandomKeyCobDto = new CobDto(
                cobCalendarDto,
                "TX123459",
                1,
                StatusOperationType.ATIVA,
                debtorDto,
                amountDto,
                "123e4567-e89b-12d3-a456-426655440000",
                "Pagamento de fatura",
                additionalInfoDtos
        );

        // Invalid key
        invalidCobDto = new CobDto(
                cobCalendarDto,
                "TX123460",
                1,
                StatusOperationType.ATIVA,
                debtorDto,
                amountDto,
                "chave-invalida",
                "Pagamento de fatura",
                additionalInfoDtos
        );
    }
}
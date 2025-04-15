package com.itau.pixms.validation.impl;

import com.itau.pixms.controller.dto.DebtorDto;
import com.itau.pixms.exception.InvalidDebtorException;
import com.itau.pixms.validation.ItemsForValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtorRulesTest {

    @Mock
    private ItemsForValidation itemsForValidation;

    @InjectMocks
    private DebtorRules debtorRules;

    private DebtorDto validDebtorDtoWithCpf;
    private DebtorDto validDebtorDtoWithCnpj;
    private DebtorDto validDebtorDtoWithoutBoth;
    private DebtorDto invalidDebtorDtoWithBoth;

    @BeforeEach
    void setUp() {
        this.buildDtos();
    }

    @Test
    void shouldPassValidationForDebtorWithOnlyCpf() {
        when(itemsForValidation.debtorDto()).thenReturn(validDebtorDtoWithCpf);

        assertDoesNotThrow(() -> debtorRules.valid(itemsForValidation));
    }

    @Test
    void shouldPassValidationForDebtorWithOnlyCnpj() {
        when(itemsForValidation.debtorDto()).thenReturn(validDebtorDtoWithCnpj);

        assertDoesNotThrow(() -> debtorRules.valid(itemsForValidation));
    }

    @Test
    void shouldThrowExceptionForDebtorWithoutAnyIdentifier() {
        when(itemsForValidation.debtorDto()).thenReturn(validDebtorDtoWithoutBoth);

        InvalidDebtorException exception = assertThrows(InvalidDebtorException.class,
                () -> debtorRules.valid(itemsForValidation));
        assertEquals("É necessário informar CPF ou CNPJ", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForDebtorWithBothCpfAndCnpj() {
        when(itemsForValidation.debtorDto()).thenReturn(invalidDebtorDtoWithBoth);

        InvalidDebtorException exception = assertThrows(InvalidDebtorException.class,
                () -> debtorRules.valid(itemsForValidation));
        assertEquals("Não é permitido preencher CPF e CNPJ simultaneamente", exception.getMessage());
    }

    private void buildDtos() {
        // Devedor apenas com CPF
        validDebtorDtoWithCpf = new DebtorDto(
                "12345678909", // CPF válido
                null, // CNPJ nulo
                "João da Silva" // nome
        );

        // Devedor apenas com CNPJ
        validDebtorDtoWithCnpj = new DebtorDto(
                null, // CPF nulo
                "12345678000199", // CNPJ válido
                "Empresa Ltda" // nome
        );

        // Devedor sem CPF e sem CNPJ (inválido - precisa ter um dos dois)
        validDebtorDtoWithoutBoth = new DebtorDto(
                null, // CPF nulo
                null, // CNPJ nulo
                "Nome do Devedor" // nome
        );

        // Devedor inválido com CPF e CNPJ (cenário de erro)
        invalidDebtorDtoWithBoth = new DebtorDto(
                "12345678909", // CPF
                "12345678000199", // CNPJ
                "Nome Inválido" // nome
        );
    }
}
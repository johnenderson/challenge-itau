package com.itau.pixconfirm.service;

import com.itau.pixconfirm.dto.ConfirmationDto;
import com.itau.pixconfirm.infrastructure.repository.CobRepository;
import com.itau.pixconfirm.type.StatusOperationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PixConfirmServiceTest {

    @Mock
    private CobRepository cobRepository;

    private PixConfirmService pixConfirmService;

    @BeforeEach
    void setUp() {
        pixConfirmService = new PixConfirmService(cobRepository);
    }

    @Test
    void shouldConfirmValidPixTransactions() {
        ConfirmationDto confirmation1 = new ConfirmationDto("tx123");
        ConfirmationDto confirmation2 = new ConfirmationDto("tx456");
        List<ConfirmationDto> confirmations = Arrays.asList(confirmation1, confirmation2);

        pixConfirmService.confirmPix(confirmations);

        verify(cobRepository).updateStatusOperationById("tx123", StatusOperationType.CONCLUIDA);
        verify(cobRepository).updateStatusOperationById("tx456", StatusOperationType.CONCLUIDA);
        verifyNoMoreInteractions(cobRepository);
    }

    @Test
    void shouldFilterInvalidConfirmations() {
        ConfirmationDto validConfirmation = new ConfirmationDto("tx123");
        ConfirmationDto nullTxIdConfirmation = new ConfirmationDto(null);
        ConfirmationDto emptyTxIdConfirmation = new ConfirmationDto("");
        ConfirmationDto blankTxIdConfirmation = new ConfirmationDto("   ");

        List<ConfirmationDto> confirmations = Arrays.asList(
                validConfirmation,
                nullTxIdConfirmation,
                emptyTxIdConfirmation,
                blankTxIdConfirmation,
                null
        );

        pixConfirmService.confirmPix(confirmations);

        verify(cobRepository, times(1)).updateStatusOperationById(anyString(), any(StatusOperationType.class));
        verify(cobRepository).updateStatusOperationById("tx123", StatusOperationType.CONCLUIDA);
        verifyNoMoreInteractions(cobRepository);
    }

    @Test
    void shouldHandleEmptyConfirmationsList() {
        List<ConfirmationDto> emptyList = Collections.emptyList();

        pixConfirmService.confirmPix(emptyList);

        verifyNoInteractions(cobRepository);
    }

    @Test
    void shouldThrowExceptionWhenConfirmationsListIsNull() {
        assertThrows(NullPointerException.class, () ->
                pixConfirmService.confirmPix(null)
        );
        verifyNoInteractions(cobRepository);
    }

    @Test
    void shouldPropagateExceptionWhenRepositoryFails() {
        ConfirmationDto confirmation = new ConfirmationDto("tx123");
        List<ConfirmationDto> confirmations = Collections.singletonList(confirmation);

        doThrow(new RuntimeException("Database error"))
                .when(cobRepository)
                .updateStatusOperationById(anyString(), any(StatusOperationType.class));

        assertThrows(RuntimeException.class, () ->
                pixConfirmService.confirmPix(confirmations)
        );

        verify(cobRepository).updateStatusOperationById("tx123", StatusOperationType.CONCLUIDA);
    }
}
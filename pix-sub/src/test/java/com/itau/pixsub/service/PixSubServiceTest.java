package com.itau.pixsub.service;

import com.itau.pixsub.client.SendPspPayment;
import com.itau.pixsub.client.dto.SendPspPaymentResponse;
import com.itau.pixsub.dto.AdditionalInfoDto;
import com.itau.pixsub.dto.AmountDto;
import com.itau.pixsub.dto.CobCalendarDto;
import com.itau.pixsub.dto.CobDto;
import com.itau.pixsub.dto.ConfirmationDto;
import com.itau.pixsub.dto.DebtorDto;
import com.itau.pixsub.type.StatusOperationType;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PixSubServiceTest {

    @Mock
    private SendPspPayment sendPspPayment;

    @Mock
    private AuthService authService;

    @Mock
    private SqsTemplate sqsTemplate;

    private PixSubService pixSubService;

    @BeforeEach
    void setUp() {
        pixSubService = new PixSubService(sendPspPayment, authService, sqsTemplate);
    }

    @Test
    void processPixShouldSendConfirmationWhenResponseIsSuccessful() {
        String token = "test-token";
        String txId1 = "txid-001";
        String txId2 = "txid-002";

        List<CobDto> cobDtos = createTestCobDtos(txId1, txId2);

        SendPspPaymentResponse successResponse = new SendPspPaymentResponse(true);
        ResponseEntity<SendPspPaymentResponse> responseEntity = ResponseEntity.ok(successResponse);

        when(authService.getToken()).thenReturn(token);
        when(sendPspPayment.sendToBacen(eq(token), any(CobDto.class))).thenReturn(responseEntity);
        CompletableFuture<SqsMessageResults> future =
                CompletableFuture.completedFuture(new SqsMessageResults("message-id"));
        doReturn(future).when(sqsTemplate).sendAsync(any(ConfirmationDto.class));

        ArgumentCaptor<ConfirmationDto> confirmationCaptor = ArgumentCaptor.forClass(ConfirmationDto.class);

        pixSubService.processPix(cobDtos);

        verify(authService, times(1)).getToken();
        verify(sendPspPayment, times(2)).sendToBacen(eq(token), any(CobDto.class));
        verify(sqsTemplate, times(2)).sendAsync(confirmationCaptor.capture());

        List<ConfirmationDto> capturedConfirmations = confirmationCaptor.getAllValues();
        assertEquals(2, capturedConfirmations.size());
        assertEquals(txId1, capturedConfirmations.get(0).txId());
        assertEquals(txId2, capturedConfirmations.get(1).txId());
    }

    @Test
    void processPixShouldNotSendConfirmationWhenResponseIsNotSuccessful() {
        String token = "test-token";
        String txId = "txid-001";

        List<CobDto> cobDtos = List.of(createTestCobDto(txId));

        SendPspPaymentResponse failureResponse = new SendPspPaymentResponse(false);
        ResponseEntity<SendPspPaymentResponse> responseEntity = ResponseEntity.ok(failureResponse);

        when(authService.getToken()).thenReturn(token);
        when(sendPspPayment.sendToBacen(eq(token), any(CobDto.class))).thenReturn(responseEntity);

        pixSubService.processPix(cobDtos);

        verify(authService, times(1)).getToken();
        verify(sendPspPayment, times(1)).sendToBacen(eq(token), any(CobDto.class));
        verify(sqsTemplate, never()).sendAsync(any(ConfirmationDto.class));
    }

    @Test
    void processPixShouldHandleEmptyList() {
        pixSubService.processPix(List.of());

        verify(authService, never()).getToken();
        verify(sendPspPayment, never()).sendToBacen(any(), any());
        verify(sqsTemplate, never()).sendAsync(any());
    }

    @Test
    void processPixShouldHandleException() {
        String token = "test-token";
        String txId = "txid-001";

        List<CobDto> cobDtos = List.of(createTestCobDto(txId));

        when(authService.getToken()).thenReturn(token);
        when(sendPspPayment.sendToBacen(eq(token), any(CobDto.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pixSubService.processPix(cobDtos);
        });

        assertTrue(exception.getMessage().contains("Failed to process PIX charge"));
        verify(authService, times(1)).getToken();
        verify(sendPspPayment, times(1)).sendToBacen(eq(token), any(CobDto.class));
        verify(sqsTemplate, never()).sendAsync(any());
    }

    private List<CobDto> createTestCobDtos(String txId1, String txId2) {
        return List.of(
                createTestCobDto(txId1),
                createTestCobDto(txId2)
        );
    }

    private CobDto createTestCobDto(String txId) {
        CobCalendarDto calendarDto = new CobCalendarDto(
                LocalDateTime.now(),
                3600
        );

        DebtorDto debtorDto = new DebtorDto(
                "12345678900",
                null,
                "Fulano de Tal"
        );

        AmountDto amountDto = new AmountDto(
                new BigDecimal("100.00"),
                0
        );

        AdditionalInfoDto additionalInfoDto = new AdditionalInfoDto(
                "pagamento",
                "Compra produto X"
        );

        return new CobDto(
                calendarDto,
                txId,
                1,
                StatusOperationType.ATIVA,
                debtorDto,
                amountDto,
                "71cdf9ba-c695-4e3c-b010-abb521a3f1cb",
                "Pagamento do produto X",
                List.of(additionalInfoDto)
        );
    }

    private static class SqsMessageResults {
        private final String messageId;

        public SqsMessageResults(String messageId) {
            this.messageId = messageId;
        }

        public String messageId() {
            return messageId;
        }
    }
}
package com.itau.pixsub.service;

import com.itau.pixsub.client.OAuth2Client;
import com.itau.pixsub.client.dto.ClientCredentialsRequest;
import com.itau.pixsub.client.dto.ClientCredentialsResponse;
import com.itau.pixsub.config.PixSubConfig;
import com.itau.pixsub.exception.GenerateTokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private OAuth2Client authClient;

    @Mock
    private PixSubConfig pixSubConfig;

    @InjectMocks
    private AuthService authService;

    private static final String CLIENT_ID = "test-client-id";
    private static final String CLIENT_SECRET = "test-client-secret";
    private static final String TEST_TOKEN = "test-access-token";
    private static final int EXPIRES_IN_SECONDS = 3600;

    @BeforeEach
    void setUp() throws Exception {
        resetStaticFields();

        when(pixSubConfig.getClientId()).thenReturn(CLIENT_ID);
        when(pixSubConfig.getClientSecret()).thenReturn(CLIENT_SECRET);
    }

    private void resetStaticFields() throws Exception {
        Field tokenField = AuthService.class.getDeclaredField("token");
        tokenField.setAccessible(true);
        tokenField.set(null, null);

        Field expiresInField = AuthService.class.getDeclaredField("expiresIn");
        expiresInField.setAccessible(true);
        expiresInField.set(null, null);
    }

    @Test
    void shouldGenerateNewTokenWhenTokenIsNull() {
        ClientCredentialsResponse tokenResponse = new ClientCredentialsResponse(TEST_TOKEN, EXPIRES_IN_SECONDS);
        ResponseEntity<ClientCredentialsResponse> response = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(authClient.authenticate(any())).thenReturn(response);
        String resultToken = authService.getToken();

        assertEquals(TEST_TOKEN, resultToken);
    }

    @Test
    void shouldThrowExceptionWhenServiceResponseIsNotSuccessful() {
        ResponseEntity<ClientCredentialsResponse> response = new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        when(authClient.authenticate(any())).thenReturn(response);
        assertThrows(GenerateTokenException.class, () -> authService.getToken());
    }

    @Test
    void shouldReuseExistingTokenWhenNotExpired() {
        ClientCredentialsResponse tokenResponse = new ClientCredentialsResponse(TEST_TOKEN, EXPIRES_IN_SECONDS);
        ResponseEntity<ClientCredentialsResponse> response = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(authClient.authenticate(any())).thenReturn(response);
        authService.getToken();
        reset(authClient);
        authService.getToken();
        verify(authClient, never()).authenticate(any());
    }

    @Test
    void shouldGenerateNewTokenWhenCurrentTokenIsExpired() throws Exception {
        ClientCredentialsResponse tokenResponse = new ClientCredentialsResponse(TEST_TOKEN, EXPIRES_IN_SECONDS);
        ResponseEntity<ClientCredentialsResponse> response = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        when(authClient.authenticate(any())).thenReturn(response);

        authService.getToken();

        Field expiresInField = AuthService.class.getDeclaredField("expiresIn");
        expiresInField.setAccessible(true);
        expiresInField.set(null, LocalDateTime.now().minusSeconds(1));

        reset(authClient);
        String newToken = "new-access-token";
        ClientCredentialsResponse newTokenResponse = new ClientCredentialsResponse(newToken, EXPIRES_IN_SECONDS);
        ResponseEntity<ClientCredentialsResponse> newResponse = new ResponseEntity<>(newTokenResponse, HttpStatus.OK);
        when(authClient.authenticate(any())).thenReturn(newResponse);

        String resultToken = authService.getToken();
        assertEquals(newToken, resultToken);
    }

    @Test
    void shouldUseClientIdFromConfiguration() {
        ClientCredentialsResponse tokenResponse = new ClientCredentialsResponse(TEST_TOKEN, EXPIRES_IN_SECONDS);
        ResponseEntity<ClientCredentialsResponse> response = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(authClient.authenticate(any())).thenReturn(response);

        authService.getToken();
        verify(pixSubConfig).getClientId();
    }

    @Test
    void shouldUseClientSecretFromConfiguration() {
        ClientCredentialsResponse tokenResponse = new ClientCredentialsResponse(TEST_TOKEN, EXPIRES_IN_SECONDS);
        ResponseEntity<ClientCredentialsResponse> response = new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        when(authClient.authenticate(any())).thenReturn(response);

        authService.getToken();
        verify(pixSubConfig).getClientSecret();
    }

    @Test
    void shouldCreateCorrectClientCredentialsRequest() {
        ClientCredentialsResponse tokenResponse = new ClientCredentialsResponse(TEST_TOKEN, EXPIRES_IN_SECONDS);
        ResponseEntity<ClientCredentialsResponse> response = new ResponseEntity<>(tokenResponse, HttpStatus.OK);

        ClientCredentialsRequest expectedRequest = new ClientCredentialsRequest(
                "client_credentials",
                CLIENT_ID,
                CLIENT_SECRET
        );

        when(authClient.authenticate(expectedRequest)).thenReturn(response);

        authService.getToken();

        verify(authClient).authenticate(expectedRequest);
    }
}
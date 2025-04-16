package com.itau.pixms.service;

import com.itau.pixms.client.OAuth2Client;
import com.itau.pixms.client.dto.ClientCredentialsRequest;
import com.itau.pixms.config.PixConfig;
import com.itau.pixms.exception.GenerateTokenException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthService {

    private static final String GRANT_TYPE = "client_credentials";
    private static String token;
    private static LocalDateTime expiresIn;

    private final OAuth2Client authClient;
    private final PixConfig pixConfig;

    public AuthService(OAuth2Client authClient, PixConfig pixConfig) {
        this.authClient = authClient;
        this.pixConfig = pixConfig;
    }

    public synchronized String getToken() {
        if (Objects.isNull(token)) {
            generateToken();
        } else if (expiresIn.isBefore(LocalDateTime.now())) {
            generateToken();
        }

        return token;
    }

    private void generateToken() {

        var request = new ClientCredentialsRequest(
                GRANT_TYPE,
                pixConfig.getClientId(),
                pixConfig.getClientSecret()
        );

        var response = authClient.authenticate(request);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new GenerateTokenException("cannot generate token, " +
                    "status: " + response.getStatusCode() +
                    "response " + response.getBody());
        }

        token = Objects.requireNonNull(response.getBody()).accessToken();
        expiresIn = LocalDateTime.now().plusSeconds(response.getBody().expiresIn());
    }

}

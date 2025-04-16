package com.itau.pixsub.service;

import com.itau.pixsub.exception.GenerateTokenException;
import com.itau.pixsub.client.OAuth2Client;
import com.itau.pixsub.client.dto.ClientCredentialsRequest;
import com.itau.pixsub.config.PixSubConfig;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class AuthService {

    private static final String GRANT_TYPE = "client_credentials";
    private static String token;
    private static LocalDateTime expiresIn;

    private final OAuth2Client authClient;
    private final PixSubConfig pixSubConfig;

    public AuthService(OAuth2Client authClient,
                       PixSubConfig pixSubConfig) {
        this.authClient = authClient;
        this.pixSubConfig = pixSubConfig;
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
                pixSubConfig.getClientId(),
                pixSubConfig.getClientSecret()
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

package com.itau.pixms.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientCredentialsResponse(@JsonProperty("access_token") String accessToken,
                                        @JsonProperty("expires_in")  Integer expiresIn) {
}

package com.itau.pixms.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ClientCredentialsRequest(@JsonProperty("grant_type")  String grantType,
                                       @JsonProperty("client_id") String clientId,
                                       @JsonProperty("client_secret") String clientSecret) {}

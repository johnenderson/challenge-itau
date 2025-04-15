package com.itau.pixms.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record CobCalendarDto(@JsonProperty("criacao")
                             @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
                             LocalDateTime createdAt,
                             @JsonProperty("expiracao")
                             Integer expiration) {
}

package com.itau.pixms.infrastructure.entity;

import java.time.LocalDateTime;

public record CobCalendar(LocalDateTime createdAt,
                          Integer expiration) {}

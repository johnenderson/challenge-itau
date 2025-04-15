package com.itau.pixms.infrastructure.entity;

import java.math.BigDecimal;

public record Amount(BigDecimal original,
                     int changeMode) {}

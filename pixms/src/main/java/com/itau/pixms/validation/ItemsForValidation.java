package com.itau.pixms.validation;

import com.itau.pixms.controller.dto.CobDto;
import com.itau.pixms.controller.dto.DebtorDto;

public record ItemsForValidation(DebtorDto debtorDto,
                                 CobDto cobDto) {
}

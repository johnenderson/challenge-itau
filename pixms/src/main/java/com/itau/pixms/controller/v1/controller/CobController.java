package com.itau.pixms.controller.v1.controller;

import com.itau.pixms.controller.dto.CobDto;
import com.itau.pixms.service.CobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CobController {

    private final CobService cobService;

    public CobController(CobService cobService) {
        this.cobService = cobService;
    }

    /**
     * Criar cobrança imediata
     */
    @PostMapping("cob")
    public ResponseEntity<Void> CreateImmediateCharge(@RequestBody @Valid CobDto dto) {

        cobService.createImmediateCharge(dto);

        return ResponseEntity.ok().build();
    }

}

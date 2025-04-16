package com.itau.pixms.controller.v1.controller;

import com.itau.pixms.controller.dto.CobDto;
import com.itau.pixms.service.CobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/cob")
public class CobController {

    private final CobService cobService;

    public CobController(CobService cobService) {
        this.cobService = cobService;
    }

    @PostMapping
    public ResponseEntity<CobDto> createCob(@RequestBody @Valid CobDto dto) {

        var cob = cobService.buildRealTimePayment(dto);

        return ResponseEntity.created(URI.create("/cob/" + cob.txId())).body(cob);
    }

    @GetMapping("/{txId}")
    public ResponseEntity<CobDto> findByTxId(@PathVariable("txId") String txId){

        var cob = cobService.findCobByTxId(txId);

        return cob.isPresent() ?
                ResponseEntity.ok(cob.get()) :
                ResponseEntity.notFound().build();
    }
}

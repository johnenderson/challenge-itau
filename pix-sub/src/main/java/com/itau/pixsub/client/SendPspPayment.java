package com.itau.pixsub.client;

import com.itau.pixsub.client.dto.SendPspPaymentResponse;
import com.itau.pixsub.dto.CobDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "SendPspPayment",
        url = "${api.dict-url}"
)
public interface SendPspPayment {

    @PostMapping(path = "/bacen/send-psp-payment")
    ResponseEntity<SendPspPaymentResponse> sendToBacen(@RequestHeader("Authorization") String token,
                                                       @RequestBody CobDto request);
}

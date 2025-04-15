package com.itau.pixms.client;

import com.itau.pixms.client.dto.FraudMarkersResponse;
import com.itau.pixms.client.dto.KeyRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "FraudMarkersClient",
        url = "${api.dict-url}"
)
public interface FraudMarkersClient {

    @PostMapping(path = "/fraud-markers")
    ResponseEntity<FraudMarkersResponse> fraudCheck(@RequestHeader("Authorization") String token,
                                                    @RequestBody KeyRequest request);
}

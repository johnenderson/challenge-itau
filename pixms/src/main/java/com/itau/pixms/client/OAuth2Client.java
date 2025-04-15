package com.itau.pixms.client;


import com.itau.pixms.client.dto.ClientCredentialsRequest;
import com.itau.pixms.client.dto.ClientCredentialsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "OAuth2Client",
        url = "${api.dict-url}"
)
public interface OAuth2Client {

    @PostMapping(path = "/token")
    ResponseEntity<ClientCredentialsResponse> authenticate(@RequestBody ClientCredentialsRequest request);
}

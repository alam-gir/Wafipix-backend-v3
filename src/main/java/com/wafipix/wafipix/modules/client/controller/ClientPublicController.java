package com.wafipix.wafipix.modules.client.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.client.dto.response.ClientResponse;
import com.wafipix.wafipix.modules.client.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/public/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientPublicController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getActiveClients() {
        log.info("Fetching active clients for public display");
        List<ClientResponse> response = clientService.getActiveClients();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable UUID id) {
        log.info("Fetching client with ID: {} for public display", id);
        ClientResponse response = clientService.getClientById(id);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/logos")
    public ResponseEntity<ApiResponse<List<String>>> getActiveClientLogos() {
        log.info("Fetching active client logos for public display");
        List<String> logos = clientService.getActiveClientLogos();
        return new ResponseEntity<>(ApiResponse.success(logos), HttpStatus.OK);
    }
}

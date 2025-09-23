package com.wafipix.wafipix.modules.client.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.client.dto.request.CreateClientRequest;
import com.wafipix.wafipix.modules.client.dto.request.UpdateClientRequest;
import com.wafipix.wafipix.modules.client.dto.response.ClientListResponse;
import com.wafipix.wafipix.modules.client.dto.response.ClientResponse;
import com.wafipix.wafipix.modules.client.service.ClientService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/admin/clients")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ClientControllerAdmin {

    private final ClientService clientService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ClientResponse>> createClient(
            @Valid @ModelAttribute CreateClientRequest request
    ) {
        log.info("Creating client with title: {}", request.getTitle());
        ClientResponse response = clientService.createClient(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getAllClients() {
        log.info("Fetching all clients");
        List<ClientResponse> response = clientService.getAllClients();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<ClientListResponse>>> getAllClientsPaginated(Pageable pageable) {
        log.info("Fetching clients with pagination");
        Page<ClientListResponse> response = clientService.getAllClients(pageable);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ClientResponse>>> getActiveClients() {
        log.info("Fetching active clients");
        List<ClientResponse> response = clientService.getActiveClients();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getClientById(@PathVariable UUID id) {
        log.info("Fetching client with ID: {}", id);
        ClientResponse response = clientService.getClientById(id);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ClientResponse>> updateClient(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateClientRequest request
    ) {
        log.info("Updating client with ID: {}", id);
        ClientResponse response = clientService.updateClient(id, request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable UUID id) {
        log.info("Deleting client with ID: {}", id);
        clientService.deleteClient(id);
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/activity-status")
    public ResponseEntity<ApiResponse<ClientResponse>> updateClientActivityStatus(
            @PathVariable UUID id,
            @RequestParam @NotNull Boolean active
    ) {
        log.info("Updating activity status for client with ID: {} to {}", id, active);
        ClientResponse response = clientService.updateClientActivityStatus(id, active);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }
}

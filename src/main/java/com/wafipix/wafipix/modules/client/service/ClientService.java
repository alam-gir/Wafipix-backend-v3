package com.wafipix.wafipix.modules.client.service;

import com.wafipix.wafipix.modules.client.dto.request.CreateClientRequest;
import com.wafipix.wafipix.modules.client.dto.request.UpdateClientRequest;
import com.wafipix.wafipix.modules.client.dto.response.ClientListResponse;
import com.wafipix.wafipix.modules.client.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ClientService {
    ClientResponse createClient(CreateClientRequest request);
    List<ClientResponse> getAllClients();
    Page<ClientListResponse> getAllClients(Pageable pageable);
    List<ClientResponse> getActiveClients();
    ClientResponse getClientById(UUID id);
    ClientResponse updateClient(UUID id, UpdateClientRequest request);
    void deleteClient(UUID id);
    ClientResponse updateClientActivityStatus(UUID id, Boolean active);
    List<String> getActiveClientLogos();
}

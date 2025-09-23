package com.wafipix.wafipix.modules.client.mapper;

import com.wafipix.wafipix.modules.client.dto.request.CreateClientRequest;
import com.wafipix.wafipix.modules.client.dto.request.UpdateClientRequest;
import com.wafipix.wafipix.modules.client.dto.response.ClientListResponse;
import com.wafipix.wafipix.modules.client.dto.response.ClientResponse;
import com.wafipix.wafipix.modules.client.entity.Client;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ClientMapper {

    public ClientResponse toResponse(Client client) {
        if (client == null) return null;

        return new ClientResponse(
                client.getId(),
                client.getTitle(),
                client.getLogo(),
                client.getDescription(),
                client.getCompanyUrl(),
                client.getActive(),
                client.getCreatedAt(),
                client.getUpdatedAt(),
                client.getCreatedBy(),
                client.getUpdatedBy()
        );
    }

    public ClientListResponse toListResponse(Client client) {
        if (client == null) return null;

        return new ClientListResponse(
                client.getId(),
                client.getTitle(),
                client.getLogo(),
                client.getDescription(),
                client.getCompanyUrl(),
                client.getActive(),
                client.getCreatedAt(),
                client.getUpdatedAt()
        );
    }

    public List<ClientResponse> toResponseList(List<Client> clients) {
        if (clients == null) return List.of();
        return clients.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ClientListResponse> toListResponseList(List<Client> clients) {
        if (clients == null) return List.of();
        return clients.stream()
                .map(this::toListResponse)
                .toList();
    }

    public Client toEntity(CreateClientRequest request, String logoUrl) {
        if (request == null) return null;

        return Client.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .companyUrl(request.getCompanyUrl())
                .logo(logoUrl)
                .active(true)
                .build();
    }

    public void updateEntity(Client client, UpdateClientRequest request, String logoUrl) {
        if (client == null || request == null) return;

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            client.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            client.setDescription(request.getDescription());
        }

        if (request.getCompanyUrl() != null) {
            client.setCompanyUrl(request.getCompanyUrl());
        }

        if (logoUrl != null) {
            client.setLogo(logoUrl);
        }
    }
}

package com.wafipix.wafipix.modules.client.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.client.dto.request.CreateClientRequest;
import com.wafipix.wafipix.modules.client.dto.request.UpdateClientRequest;
import com.wafipix.wafipix.modules.client.dto.response.ClientListResponse;
import com.wafipix.wafipix.modules.client.dto.response.ClientResponse;
import com.wafipix.wafipix.modules.client.entity.Client;
import com.wafipix.wafipix.modules.client.mapper.ClientMapper;
import com.wafipix.wafipix.modules.client.repository.ClientRepository;
import com.wafipix.wafipix.modules.client.service.ClientService;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final FileService fileService;

    @Override
    @Transactional
    public ClientResponse createClient(CreateClientRequest request) {
        log.info("Creating client with title: {}", request.getTitle());

        // Check if client with same title already exists
        if (clientRepository.existsByTitleIgnoreCase(request.getTitle())) {
            throw new BusinessException("Client with title '" + request.getTitle() + "' already exists");
        }

        // Upload logo
        String logoUrl;
        try {
            var uploadedFile = fileService.uploadFile(request.getLogo(), "clients/logos");
            logoUrl = uploadedFile.getPublicUrl();
            log.info("Logo uploaded successfully: {}", logoUrl);
        } catch (Exception e) {
            log.error("Failed to upload logo: {}", e.getMessage());
            throw new BusinessException("Failed to upload logo: " + e.getMessage());
        }

        // Create client entity
        Client client = clientMapper.toEntity(request, logoUrl);
        Client savedClient = clientRepository.save(client);

        log.info("Client created successfully with ID: {}", savedClient.getId());
        return clientMapper.toResponse(savedClient);
    }

    @Override
    public List<ClientResponse> getAllClients() {
        log.info("Fetching all clients");

        List<Client> clients = clientRepository.findAll();
        log.info("Found {} clients", clients.size());

        return clientMapper.toResponseList(clients);
    }

    @Override
    public Page<ClientListResponse> getAllClients(Pageable pageable) {
        log.info("Fetching clients with pagination");

        Page<Client> clientPage = clientRepository.findAllOrdered(pageable);
        Page<ClientListResponse> response = clientPage.map(clientMapper::toListResponse);

        log.info("Found {} clients", clientPage.getTotalElements());
        return response;
    }

    @Override
    public List<ClientResponse> getActiveClients() {
        log.info("Fetching active clients");

        List<Client> clients = clientRepository.findActiveClients();
        log.info("Found {} active clients", clients.size());

        return clientMapper.toResponseList(clients);
    }

    @Override
    public ClientResponse getClientById(UUID id) {
        log.info("Fetching client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional
    public ClientResponse updateClient(UUID id, UpdateClientRequest request) {
        log.info("Updating client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        // Check if another client with same title already exists
        if (request.getTitle() != null && clientRepository.existsByTitleIgnoreCaseAndIdNot(request.getTitle(), id)) {
            throw new BusinessException("Client with title '" + request.getTitle() + "' already exists");
        }

        // Handle logo update if provided
        String logoUrl = null;
        if (request.getLogo() != null && !request.getLogo().isEmpty()) {
            try {
                // Delete old logo if exists
                if (client.getLogo() != null) {
                    fileService.deleteFileByUrl(client.getLogo());
                    log.info("Old logo deleted: {}", client.getLogo());
                }

                // Upload new logo
                var uploadedFile = fileService.uploadFile(request.getLogo(), "clients/logos");
                logoUrl = uploadedFile.getPublicUrl();
                log.info("New logo uploaded successfully: {}", logoUrl);
            } catch (Exception e) {
                log.error("Failed to update logo: {}", e.getMessage());
                throw new BusinessException("Failed to update logo: " + e.getMessage());
            }
        }

        // Update client
        clientMapper.updateEntity(client, request, logoUrl);
        Client updatedClient = clientRepository.save(client);

        log.info("Client updated successfully with ID: {}", updatedClient.getId());
        return clientMapper.toResponse(updatedClient);
    }

    @Override
    @Transactional
    public void deleteClient(UUID id) {
        log.info("Deleting client with ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        // Delete logo from file storage
        if (client.getLogo() != null) {
            try {
                boolean deleted = fileService.deleteFileByUrl(client.getLogo());
                if (deleted) {
                    log.info("Logo deleted from storage: {}", client.getLogo());
                } else {
                    log.warn("Failed to delete logo from storage: {}", client.getLogo());
                }
            } catch (Exception e) {
                log.error("Error deleting logo from storage: {}", e.getMessage());
                // Continue with client deletion even if logo deletion fails
            }
        }

        // Delete client
        clientRepository.delete(client);

        log.info("Client deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public ClientResponse updateClientActivityStatus(UUID id, Boolean active) {
        log.info("Updating activity status for client with ID: {} to {}", id, active);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + id));

        client.setActive(active);
        Client updatedClient = clientRepository.save(client);

        log.info("Client activity status updated for ID: {} to {}", id, active);
        return clientMapper.toResponse(updatedClient);
    }

    @Override
    public List<String> getActiveClientLogos() {
        log.info("Fetching active client logos");

        List<Client> activeClients = clientRepository.findActiveClients();
        List<String> logos = activeClients.stream()
                .map(Client::getLogo)
                .filter(logo -> logo != null && !logo.trim().isEmpty())
                .toList();

        log.info("Found {} active client logos", logos.size());
        return logos;
    }
}

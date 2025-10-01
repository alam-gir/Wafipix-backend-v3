package com.wafipix.wafipix.modules.contact.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.contact.dto.request.ContactFormRequest;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponsePublic;
import com.wafipix.wafipix.modules.contact.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v3/public/contacts")
@RequiredArgsConstructor
@Slf4j
public class ContactPublicController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponsePublic>> submitContactForm(
            @Valid @RequestBody ContactFormRequest request
    ) {
        log.info("Submitting contact form from: {}", request.getEmail());
        ContactResponsePublic response = contactService.submitPublicContactForm(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }
}

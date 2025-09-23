package com.wafipix.wafipix.modules.contact.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.contact.dto.request.ContactReplyRequest;
import com.wafipix.wafipix.modules.contact.dto.response.ContactListResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponse;
import com.wafipix.wafipix.modules.contact.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/admin/contacts")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ContactControllerAdmin {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactResponse>>> getAllContacts() {
        log.info("Fetching all contacts");
        List<ContactResponse> response = contactService.getAllContacts();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<ContactListResponse>>> getAllContactsPaginated(Pageable pageable) {
        log.info("Fetching contacts with pagination");
        Page<ContactListResponse> response = contactService.getAllContacts(pageable);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactResponse>> getContactById(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        log.info("Fetching contact with ID: {}", id);
        String readBy = authentication.getName();
        ContactResponse response = contactService.getContactById(id, readBy);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<ContactResponse>> replyToContact(
            @PathVariable UUID id,
            @Valid @RequestBody ContactReplyRequest request,
            Authentication authentication
    ) {
        log.info("Replying to contact with ID: {}", id);
        String repliedBy = authentication.getName();
        ContactResponse response = contactService.replyToContact(id, request, repliedBy);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable UUID id) {
        log.info("Deleting contact with ID: {}", id);
        contactService.deleteContact(id);
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.NO_CONTENT);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadContactCount() {
        log.info("Fetching unread contact count");
        long count = contactService.getUnreadContactCount();
        return new ResponseEntity<>(ApiResponse.success(count), HttpStatus.OK);
    }
}

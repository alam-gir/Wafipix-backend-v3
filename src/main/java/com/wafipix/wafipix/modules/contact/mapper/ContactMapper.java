package com.wafipix.wafipix.modules.contact.mapper;

import com.wafipix.wafipix.modules.contact.dto.request.ContactFormRequest;
import com.wafipix.wafipix.modules.contact.dto.request.ContactReplyRequest;
import com.wafipix.wafipix.modules.contact.dto.response.ContactListResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactReplyResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponsePublic;
import com.wafipix.wafipix.modules.contact.entity.Contact;
import com.wafipix.wafipix.modules.contact.entity.ContactReply;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContactMapper {

    public ContactResponse toResponse(Contact contact) {
        if (contact == null) return null;

        List<ContactReplyResponse> replies = contact.getReplies() != null 
            ? contact.getReplies().stream()
                .map(this::toReplyResponse)
                .toList()
            : List.of();

        return new ContactResponse(
                contact.getId(),
                contact.getFullName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getMessage(),
                contact.getStatus(),
                contact.getReadBy(),
                replies,
                contact.getCreatedAt(),
                contact.getUpdatedAt(),
                contact.getCreatedBy(),
                contact.getUpdatedBy()
        );
    }

    public ContactListResponse toListResponse(Contact contact) {
        if (contact == null) return null;

        return new ContactListResponse(
                contact.getId(),
                contact.getFullName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getMessage(),
                contact.getStatus(),
                contact.getReadBy(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }

    public ContactReplyResponse toReplyResponse(ContactReply reply) {
        if (reply == null) return null;

        return new ContactReplyResponse(
                reply.getId(),
                reply.getMessage(),
                reply.getRepliedBy(),
                reply.getCreatedAt(),
                reply.getUpdatedAt()
        );
    }

    public List<ContactResponse> toResponseList(List<Contact> contacts) {
        if (contacts == null) return List.of();
        return contacts.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ContactListResponse> toListResponseList(List<Contact> contacts) {
        if (contacts == null) return List.of();
        return contacts.stream()
                .map(this::toListResponse)
                .toList();
    }

    public Contact toEntity(ContactFormRequest request) {
        if (request == null) return null;

        return Contact.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .message(request.getMessage())
                .status("new")
                .build();
    }

    public ContactReply toReplyEntity(ContactReplyRequest request, Contact contact, String repliedBy) {
        if (request == null || contact == null) return null;

        return ContactReply.builder()
                .contact(contact)
                .message(request.getMessage())
                .repliedBy(repliedBy)
                .build();
    }

    // Public API mapping method
    public ContactResponsePublic toPublicResponse(Contact contact) {
        if (contact == null) return null;

        return new ContactResponsePublic(
                contact.getId(),
                contact.getFullName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getMessage()
        );
    }
}

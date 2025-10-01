package com.wafipix.wafipix.modules.contact.service;

import com.wafipix.wafipix.modules.contact.dto.request.ContactFormRequest;
import com.wafipix.wafipix.modules.contact.dto.request.ContactReplyRequest;
import com.wafipix.wafipix.modules.contact.dto.response.ContactListResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponsePublic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ContactService {
    ContactResponse submitContactForm(ContactFormRequest request);
    
    // Public API method
    ContactResponsePublic submitPublicContactForm(ContactFormRequest request);
    
    List<ContactResponse> getAllContacts();
    Page<ContactListResponse> getAllContacts(Pageable pageable);
    ContactResponse getContactById(UUID id, String readBy);
    ContactResponse replyToContact(UUID contactId, ContactReplyRequest request, String repliedBy);
    void deleteContact(UUID id);
    long getUnreadContactCount();
}

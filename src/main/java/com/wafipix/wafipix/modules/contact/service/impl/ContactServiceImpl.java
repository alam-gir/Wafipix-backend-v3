package com.wafipix.wafipix.modules.contact.service.impl;

import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.contact.dto.request.ContactFormRequest;
import com.wafipix.wafipix.modules.contact.dto.request.ContactReplyRequest;
import com.wafipix.wafipix.modules.contact.dto.response.ContactListResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponsePublic;
import com.wafipix.wafipix.modules.contact.entity.Contact;
import com.wafipix.wafipix.modules.contact.entity.ContactReply;
import com.wafipix.wafipix.modules.contact.event.ContactFormSubmittedEvent;
import com.wafipix.wafipix.modules.contact.event.ContactReplySentEvent;
import com.wafipix.wafipix.modules.contact.mapper.ContactMapper;
import com.wafipix.wafipix.modules.contact.repository.ContactReplyRepository;
import com.wafipix.wafipix.modules.contact.repository.ContactRepository;
import com.wafipix.wafipix.modules.contact.service.ContactService;
import com.wafipix.wafipix.modules.email.dto.EmailRequest;
import com.wafipix.wafipix.modules.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;
    private final ContactReplyRepository contactReplyRepository;
    private final ContactMapper contactMapper;
    private final EmailService emailService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${contact.notify.email}")
    private String notifyEmail;

    @Value("${contact.admin.url}")
    private String adminUrl;

    @Override
    @Transactional
    public ContactResponse submitContactForm(ContactFormRequest request) {
        log.info("Submitting contact form from: {}", request.getEmail());

        // Create contact entity
        Contact contact = contactMapper.toEntity(request);
        // Set audit fields for public contact form
        contact.setCreatedBy("public-contact-form");
        contact.setUpdatedBy("public-contact-form");
        Contact savedContact = contactRepository.save(contact);

        // Publish event for asynchronous email sending
        eventPublisher.publishEvent(new ContactFormSubmittedEvent(this, savedContact));

        log.info("Contact form submitted successfully with ID: {} - Emails will be sent asynchronously", savedContact.getId());
        return contactMapper.toResponse(savedContact);
    }

    @Override
    public List<ContactResponse> getAllContacts() {
        log.info("Fetching all contacts");

        List<Contact> contacts = contactRepository.findAll();
        log.info("Found {} contacts", contacts.size());

        return contactMapper.toResponseList(contacts);
    }

    @Override
    public Page<ContactListResponse> getAllContacts(Pageable pageable) {
        log.info("Fetching contacts with pagination");

        Page<Contact> contactPage = contactRepository.findAllOrdered(pageable);
        Page<ContactListResponse> response = contactPage.map(contactMapper::toListResponse);

        log.info("Found {} contacts", contactPage.getTotalElements());
        return response;
    }

    @Override
    @Transactional
    public ContactResponse getContactById(UUID id, String readBy) {
        log.info("Fetching contact with ID: {} by user: {}", id, readBy);

        Contact contact = contactRepository.findByIdWithReplies(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));

        // Mark as read if not already read
        if (contact.getReadBy() == null) {
            contact.setReadBy(readBy);
            contact.setStatus("read");
            contactRepository.save(contact);
            log.info("Contact marked as read by: {}", readBy);
        }

        return contactMapper.toResponse(contact);
    }

    @Override
    @Transactional
    public ContactResponse replyToContact(UUID contactId, ContactReplyRequest request, String repliedBy) {
        log.info("Replying to contact with ID: {} by user: {}", contactId, repliedBy);

        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + contactId));

        // Create reply entity
        ContactReply reply = contactMapper.toReplyEntity(request, contact, repliedBy);
        ContactReply savedReply = contactReplyRepository.save(reply);

        // Update contact status
        contact.setStatus("replied");
        contactRepository.save(contact);

        // Publish event for asynchronous reply email sending
        eventPublisher.publishEvent(new ContactReplySentEvent(this, contact, savedReply));

        log.info("Reply sent successfully for contact ID: {} - Email will be sent asynchronously", contactId);
        return contactMapper.toResponse(contact);
    }

    @Override
    @Transactional
    public void deleteContact(UUID id) {
        log.info("Deleting contact with ID: {}", id);

        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contact not found with ID: " + id));

        contactRepository.delete(contact);
        log.info("Contact deleted successfully with ID: {}", id);
    }

    @Override
    public long getUnreadContactCount() {
        return contactRepository.countUnreadContacts();
    }
}

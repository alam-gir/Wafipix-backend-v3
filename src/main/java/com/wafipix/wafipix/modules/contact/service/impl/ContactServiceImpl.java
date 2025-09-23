package com.wafipix.wafipix.modules.contact.service.impl;

import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.contact.dto.request.ContactFormRequest;
import com.wafipix.wafipix.modules.contact.dto.request.ContactReplyRequest;
import com.wafipix.wafipix.modules.contact.dto.response.ContactListResponse;
import com.wafipix.wafipix.modules.contact.dto.response.ContactResponse;
import com.wafipix.wafipix.modules.contact.entity.Contact;
import com.wafipix.wafipix.modules.contact.entity.ContactReply;
import com.wafipix.wafipix.modules.contact.mapper.ContactMapper;
import com.wafipix.wafipix.modules.contact.repository.ContactReplyRepository;
import com.wafipix.wafipix.modules.contact.repository.ContactRepository;
import com.wafipix.wafipix.modules.contact.service.ContactService;
import com.wafipix.wafipix.modules.email.dto.EmailRequest;
import com.wafipix.wafipix.modules.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

        // Send notification email to admin
        sendAdminNotification(savedContact);

        // Send confirmation email to visitor
        sendVisitorConfirmation(savedContact);

        log.info("Contact form submitted successfully with ID: {}", savedContact.getId());
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

        // Send reply email to visitor
        sendReplyToVisitor(contact, savedReply);

        log.info("Reply sent successfully for contact ID: {}", contactId);
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

    private void sendAdminNotification(Contact contact) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("contactId", contact.getId());
            variables.put("fullName", contact.getFullName());
            variables.put("email", contact.getEmail());
            variables.put("phone", contact.getPhone());
            variables.put("message", contact.getMessage());
            variables.put("adminUrl", adminUrl);

            boolean sent = emailService.sendTemplateEmail(
                    notifyEmail,
                    "New Contact Form Submission - " + contact.getFullName(),
                    "contact-notification",
                    variables
            );

            if (sent) {
                log.info("Admin notification sent for contact ID: {}", contact.getId());
            } else {
                log.error("Failed to send admin notification for contact ID: {}", contact.getId());
            }
        } catch (Exception e) {
            log.error("Error sending admin notification: {}", e.getMessage());
        }
    }

    private void sendVisitorConfirmation(Contact contact) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", contact.getFullName());
            variables.put("message", contact.getMessage());

            boolean sent = emailService.sendTemplateEmail(
                    contact.getEmail(),
                    "Thank you for contacting us - Wafipix",
                    "contact-confirmation",
                    variables
            );

            if (sent) {
                log.info("Confirmation email sent to: {}", contact.getEmail());
            } else {
                log.error("Failed to send confirmation email to: {}", contact.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending confirmation email: {}", e.getMessage());
        }
    }

    private void sendReplyToVisitor(Contact contact, ContactReply reply) {
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", contact.getFullName());
            variables.put("originalMessage", contact.getMessage());
            variables.put("replyMessage", reply.getMessage());
            variables.put("repliedBy", reply.getRepliedBy());

            boolean sent = emailService.sendTemplateEmail(
                    contact.getEmail(),
                    "Reply to your inquiry - Wafipix",
                    "contact-reply",
                    variables
            );

            if (sent) {
                log.info("Reply email sent to: {}", contact.getEmail());
            } else {
                log.error("Failed to send reply email to: {}", contact.getEmail());
            }
        } catch (Exception e) {
            log.error("Error sending reply email: {}", e.getMessage());
        }
    }
}

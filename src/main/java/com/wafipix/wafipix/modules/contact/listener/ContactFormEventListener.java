package com.wafipix.wafipix.modules.contact.listener;

import com.wafipix.wafipix.modules.contact.event.ContactFormSubmittedEvent;
import com.wafipix.wafipix.modules.contact.event.ContactReplySentEvent;
import com.wafipix.wafipix.modules.contact.entity.Contact;
import com.wafipix.wafipix.modules.contact.entity.ContactReply;
import com.wafipix.wafipix.modules.email.dto.EmailRequest;
import com.wafipix.wafipix.modules.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContactFormEventListener {

    private final EmailService emailService;

    @Value("${contact.notify.email}")
    private String notifyEmail;

    @Value("${contact.admin.url}")
    private String adminUrl;

    @EventListener
    @Async
    public CompletableFuture<Void> handleContactFormSubmitted(ContactFormSubmittedEvent event) {
        Contact contact = event.getContact();
        
        log.info("Processing contact form submission asynchronously for contact ID: {}", contact.getId());
        
        try {
            // Send notification email to admin
            sendAdminNotification(contact);
            
            // Send confirmation email to visitor
            sendVisitorConfirmation(contact);
            
            log.info("Successfully sent emails for contact ID: {}", contact.getId());
        } catch (Exception e) {
            log.error("Failed to send emails for contact ID: {} - Error: {}", contact.getId(), e.getMessage(), e);
        }
        
        return CompletableFuture.completedFuture(null);
    }

    @EventListener
    @Async
    public CompletableFuture<Void> handleContactReplySent(ContactReplySentEvent event) {
        Contact contact = event.getContact();
        ContactReply reply = event.getReply();
        
        log.info("Processing contact reply asynchronously for contact ID: {}", contact.getId());
        
        try {
            // Send reply email to visitor
            sendReplyToVisitor(contact, reply);
            
            log.info("Successfully sent reply email for contact ID: {}", contact.getId());
        } catch (Exception e) {
            log.error("Failed to send reply email for contact ID: {} - Error: {}", contact.getId(), e.getMessage(), e);
        }
        
        return CompletableFuture.completedFuture(null);
    }

    private void sendAdminNotification(Contact contact) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("contact", contact);
            templateData.put("adminUrl", adminUrl);

            EmailRequest emailRequest = EmailRequest.builder()
                    .to(notifyEmail)
                    .subject("New Contact Form Submission - " + contact.getFullName())
                    .templateName("contact-notification")
                    .templateVariables(templateData)
                    .build();

            emailService.sendEmail(emailRequest);
            log.info("Admin notification email sent for contact ID: {}", contact.getId());
        } catch (Exception e) {
            log.error("Failed to send admin notification email for contact ID: {} - Error: {}", contact.getId(), e.getMessage());
        }
    }

    private void sendVisitorConfirmation(Contact contact) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("contact", contact);

            EmailRequest emailRequest = EmailRequest.builder()
                    .to(contact.getEmail())
                    .subject("Thank you for contacting us!")
                    .templateName("contact-confirmation")
                    .templateVariables(templateData)
                    .build();

            emailService.sendEmail(emailRequest);
            log.info("Visitor confirmation email sent for contact ID: {}", contact.getId());
        } catch (Exception e) {
            log.error("Failed to send visitor confirmation email for contact ID: {} - Error: {}", contact.getId(), e.getMessage());
        }
    }

    private void sendReplyToVisitor(Contact contact, ContactReply reply) {
        try {
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("contact", contact);
            templateData.put("reply", reply);

            EmailRequest emailRequest = EmailRequest.builder()
                    .to(contact.getEmail())
                    .subject("Reply to your inquiry - Wafipix")
                    .templateName("contact-reply")
                    .templateVariables(templateData)
                    .build();

            emailService.sendEmail(emailRequest);
            log.info("Reply email sent for contact ID: {}", contact.getId());
        } catch (Exception e) {
            log.error("Failed to send reply email for contact ID: {} - Error: {}", contact.getId(), e.getMessage());
        }
    }
}

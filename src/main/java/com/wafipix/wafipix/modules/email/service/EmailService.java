package com.wafipix.wafipix.modules.email.service;

import com.wafipix.wafipix.modules.email.dto.EmailRequest;

public interface EmailService {
    
    /**
     * Send email with the provided request
     * @param emailRequest Email request containing all email details
     * @return true if email sent successfully, false otherwise
     */
    boolean sendEmail(EmailRequest emailRequest);
    
    /**
     * Send simple text email
     * @param to Recipient email address
     * @param subject Email subject
     * @param textContent Plain text content
     * @return true if email sent successfully, false otherwise
     */
    boolean sendTextEmail(String to, String subject, String textContent);
    
    /**
     * Send HTML email
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content
     * @return true if email sent successfully, false otherwise
     */
    boolean sendHtmlEmail(String to, String subject, String htmlContent);
    
    /**
     * Send email using template
     * @param to Recipient email address
     * @param subject Email subject
     * @param templateName Template name
     * @param variables Template variables
     * @return true if email sent successfully, false otherwise
     */
    boolean sendTemplateEmail(String to, String subject, String templateName, java.util.Map<String, Object> variables);
    
    /**
     * Send email with attachments
     * @param to Recipient email address
     * @param subject Email subject
     * @param htmlContent HTML content
     * @param attachments List of attachment file paths
     * @return true if email sent successfully, false otherwise
     */
    boolean sendEmailWithAttachments(String to, String subject, String htmlContent, java.util.List<String> attachments);
}

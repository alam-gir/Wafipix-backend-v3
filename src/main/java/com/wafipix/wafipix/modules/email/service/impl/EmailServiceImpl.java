package com.wafipix.wafipix.modules.email.service.impl;

import com.wafipix.wafipix.modules.email.dto.EmailRequest;
import com.wafipix.wafipix.modules.email.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine emailTemplateEngine;

    public EmailServiceImpl(JavaMailSender mailSender, @Qualifier("emailTemplateEngine") TemplateEngine emailTemplateEngine) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
    }

    @Value("${spring.mail.from:${spring.mail.username}}")
    private String fromEmail;

    @Override
    public boolean sendEmail(EmailRequest emailRequest) {
        try {
            if (emailRequest.getHtmlContent() != null || emailRequest.getAttachments() != null || emailRequest.getTemplateName() != null) {
                return sendMimeMessage(emailRequest);
            } else {
                return sendSimpleMessage(emailRequest);
            }
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", emailRequest.getTo(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendTextEmail(String to, String subject, String textContent) {
        EmailRequest request = EmailRequest.simpleText(to, subject, textContent).build();
        return sendEmail(request);
    }

    @Override
    public boolean sendHtmlEmail(String to, String subject, String htmlContent) {
        EmailRequest request = EmailRequest.htmlEmail(to, subject, htmlContent).build();
        return sendEmail(request);
    }

    @Override
    public boolean sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        EmailRequest request = EmailRequest.templateEmail(to, subject, templateName, variables).build();
        return sendEmail(request);
    }

    @Override
    public boolean sendEmailWithAttachments(String to, String subject, String htmlContent, List<String> attachments) {
        EmailRequest request = EmailRequest.builder()
                .to(to)
                .subject(subject)
                .htmlContent(htmlContent)
                .attachments(attachments)
                .build();
        return sendEmail(request);
    }

    private boolean sendSimpleMessage(EmailRequest emailRequest) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(emailRequest.getTo());
            message.setSubject(emailRequest.getSubject());
            message.setText(emailRequest.getTextContent());

            if (emailRequest.getCc() != null) {
                message.setCc(emailRequest.getCc());
            }
            if (emailRequest.getBcc() != null) {
                message.setBcc(emailRequest.getBcc());
            }

            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", emailRequest.getTo());
            return true;
        } catch (Exception e) {
            log.error("Failed to send simple email: {}", e.getMessage());
            return false;
        }
    }

    private boolean sendMimeMessage(EmailRequest emailRequest) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());

        if (emailRequest.getCc() != null) {
            helper.setCc(emailRequest.getCc());
        }
        if (emailRequest.getBcc() != null) {
            helper.setBcc(emailRequest.getBcc());
        }

        // Handle template-based content
        if (emailRequest.getTemplateName() != null) {
            String htmlContent = processTemplate(emailRequest.getTemplateName(), emailRequest.getTemplateVariables());
            helper.setText(htmlContent, true);
        } else if (emailRequest.getHtmlContent() != null) {
            helper.setText(emailRequest.getHtmlContent(), true);
        } else if (emailRequest.getTextContent() != null) {
            helper.setText(emailRequest.getTextContent(), false);
        }

        // Handle attachments
        if (emailRequest.getAttachments() != null && !emailRequest.getAttachments().isEmpty()) {
            for (String attachmentPath : emailRequest.getAttachments()) {
                FileSystemResource file = new FileSystemResource(new File(attachmentPath));
                helper.addAttachment(file.getFilename(), file);
            }
        }

        mailSender.send(mimeMessage);
        log.info("MIME email sent successfully to: {}", emailRequest.getTo());
        return true;
    }

    private String processTemplate(String templateName, Map<String, Object> variables) {
        Context context = new Context();
        if (variables != null) {
            context.setVariables(variables);
        }
        return emailTemplateEngine.process(templateName, context);
    }
}

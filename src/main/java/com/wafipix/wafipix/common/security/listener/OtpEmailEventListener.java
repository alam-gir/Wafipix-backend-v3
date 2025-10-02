package com.wafipix.wafipix.common.security.listener;

import com.wafipix.wafipix.common.security.event.OtpEmailSentEvent;
import com.wafipix.wafipix.modules.email.dto.EmailRequest;
import com.wafipix.wafipix.modules.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class OtpEmailEventListener {

    private final EmailService emailService;

    @EventListener
    @Async("otpEmailExecutor")
    public CompletableFuture<Void> handleOtpEmailSent(OtpEmailSentEvent event) {
        String email = event.getEmail();
        String otpCode = event.getOtpCode();
        int expirationMinutes = event.getExpirationMinutes();

        log.info("Processing OTP email sending asynchronously for email: {}", email);

        try {
            sendOtpEmail(email, otpCode, expirationMinutes);
            log.info("Successfully sent OTP email for email: {}", email);
        } catch (Exception e) {
            log.error("Failed to send OTP email for email: {} - Error: {}", email, e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }

    private void sendOtpEmail(String email, String otpCode, int expirationMinutes) {
        try {
            Map<String, Object> templateVariables = new HashMap<>();
            templateVariables.put("otpCode", otpCode);
            templateVariables.put("expirationMinutes", expirationMinutes);
            templateVariables.put("email", email);

            EmailRequest emailRequest = EmailRequest.builder()
                    .to(email)
                    .subject("Your Wafipix Login Code")
                    .templateName("otp-login")
                    .templateVariables(templateVariables)
                    .build();

            boolean emailSent = emailService.sendEmail(emailRequest);
            
            if (emailSent) {
                log.info("OTP email sent successfully to: {}", email);
            } else {
                log.error("Failed to send OTP email to: {}", email);
            }
        } catch (Exception e) {
            log.error("Error sending OTP email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }
}

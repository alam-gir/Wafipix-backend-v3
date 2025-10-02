package com.wafipix.wafipix.common.security.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OtpEmailSentEvent extends ApplicationEvent {
    private final String email;
    private final String otpCode;
    private final int expirationMinutes;

    public OtpEmailSentEvent(Object source, String email, String otpCode, int expirationMinutes) {
        super(source);
        this.email = email;
        this.otpCode = otpCode;
        this.expirationMinutes = expirationMinutes;
    }
}

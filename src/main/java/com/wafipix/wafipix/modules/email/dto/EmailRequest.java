package com.wafipix.wafipix.modules.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {
    
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String textContent;
    private String htmlContent;
    private List<String> attachments;
    private Map<String, Object> templateVariables;
    private String templateName;
    
    // Builder methods for common use cases
    public static EmailRequestBuilder simpleText(String to, String subject, String textContent) {
        return EmailRequest.builder()
                .to(to)
                .subject(subject)
                .textContent(textContent);
    }
    
    public static EmailRequestBuilder htmlEmail(String to, String subject, String htmlContent) {
        return EmailRequest.builder()
                .to(to)
                .subject(subject)
                .htmlContent(htmlContent);
    }
    
    public static EmailRequestBuilder templateEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        return EmailRequest.builder()
                .to(to)
                .subject(subject)
                .templateName(templateName)
                .templateVariables(variables);
    }
}

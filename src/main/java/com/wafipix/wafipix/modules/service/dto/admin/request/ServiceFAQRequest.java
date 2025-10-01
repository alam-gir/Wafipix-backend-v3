package com.wafipix.wafipix.modules.service.dto.admin.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFAQRequest {
    
    private UUID id; // Optional - if present, update existing; if null, create new
    
    @NotBlank(message = "Question is required")
    @Size(max = 500, message = "Question must not exceed 500 characters")
    private String question;
    
    @NotBlank(message = "Answer is required")
    @Size(max = 2000, message = "Answer must not exceed 2000 characters")
    private String answer;
}

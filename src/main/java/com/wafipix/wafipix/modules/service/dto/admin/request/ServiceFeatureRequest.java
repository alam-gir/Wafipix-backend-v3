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
public class ServiceFeatureRequest {
    
    private UUID id; // Optional - if present, update existing; if null, create new
    
    @NotBlank(message = "Feature text is required")
    @Size(max = 500, message = "Feature text must not exceed 500 characters")
    private String text;
    
    private Boolean highlight;
}

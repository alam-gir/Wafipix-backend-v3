package com.wafipix.wafipix.modules.contact.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactReplyRequest {

    @NotBlank(message = "Reply message is required")
    @Size(max = 5000, message = "Reply message must not exceed 5000 characters")
    private String message;
}

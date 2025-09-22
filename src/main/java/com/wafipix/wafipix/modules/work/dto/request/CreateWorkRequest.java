package com.wafipix.wafipix.modules.work.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWorkRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotNull(message = "Service ID is required")
    private String serviceId;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    // File uploads - at least one cover and one profile media required
    private MultipartFile coverVideo;
    private MultipartFile coverImage;
    private MultipartFile profileVideo;
    private MultipartFile profileImage;
}

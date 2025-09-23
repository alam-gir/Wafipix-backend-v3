package com.wafipix.wafipix.modules.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
public class CreateReviewRequest {

    @NotBlank(message = "Platform is required")
    @Size(max = 100, message = "Platform must not exceed 100 characters")
    private String platform;

    @Size(max = 100, message = "Client name must not exceed 100 characters")
    private String clientName;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    @Size(max = 5000, message = "Review text must not exceed 5000 characters")
    private String reviewText;

    private MultipartFile reviewImage;
}

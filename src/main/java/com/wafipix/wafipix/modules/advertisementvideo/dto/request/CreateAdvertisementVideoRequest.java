package com.wafipix.wafipix.modules.advertisementvideo.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAdvertisementVideoRequest {

    @NotNull(message = "Video file is required")
    private MultipartFile videoFile;
}

package com.wafipix.wafipix.modules.work.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGalleryRequest {

    @Builder.Default
    private Boolean isMobileGrid = false;

    private List<MultipartFile> files;
}

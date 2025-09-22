package com.wafipix.wafipix.modules.work.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGalleryRequest {

    private Boolean isMobileGrid;
}

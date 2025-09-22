package com.wafipix.wafipix.modules.work.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemoveGalleryFilesRequest {

    @NotNull(message = "Gallery item IDs cannot be null")
    @NotEmpty(message = "At least one gallery item ID must be provided")
    private List<UUID> galleryItemIds;
}

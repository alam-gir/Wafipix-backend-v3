package com.wafipix.wafipix.modules.work.mapper;

import com.wafipix.wafipix.modules.work.dto.response.GalleryResponse;
import com.wafipix.wafipix.modules.work.entity.Gallery;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GalleryMapper {

    private final GalleryItemMapper galleryItemMapper;

    public GalleryMapper(GalleryItemMapper galleryItemMapper) {
        this.galleryItemMapper = galleryItemMapper;
    }

    public GalleryResponse toResponse(Gallery gallery) {
        if (gallery == null) return null;

        return new GalleryResponse(
                gallery.getId(),
                gallery.getIsMobileGrid(),
                gallery.getItems() != null ? 
                    gallery.getItems().stream()
                        .map(galleryItemMapper::toResponse)
                        .toList() : List.of(),
                gallery.getCreatedAt(),
                gallery.getUpdatedAt()
        );
    }
}

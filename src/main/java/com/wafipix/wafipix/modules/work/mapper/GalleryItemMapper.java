package com.wafipix.wafipix.modules.work.mapper;

import com.wafipix.wafipix.modules.filemanagement.mapper.FileMapper;
import com.wafipix.wafipix.modules.work.dto.response.GalleryItemResponse;
import com.wafipix.wafipix.modules.work.entity.GalleryItem;
import org.springframework.stereotype.Component;

@Component
public class GalleryItemMapper {

    private final FileMapper fileMapper;

    public GalleryItemMapper(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public GalleryItemResponse toResponse(GalleryItem galleryItem) {
        if (galleryItem == null) return null;

        return new GalleryItemResponse(
                galleryItem.getId(),
                fileMapper.toResponse(galleryItem.getFile()),
                galleryItem.getCreatedAt(),
                galleryItem.getUpdatedAt()
        );
    }
}

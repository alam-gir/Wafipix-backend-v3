package com.wafipix.wafipix.modules.work.mapper;

import com.wafipix.wafipix.modules.filemanagement.mapper.FileMapper;
import com.wafipix.wafipix.modules.service.mapper.ServiceMapper;
import com.wafipix.wafipix.modules.work.dto.response.WorkListResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkListPublicResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkDetailPublicResponse;
import com.wafipix.wafipix.modules.work.dto.response.GalleryPublicResponse;
import com.wafipix.wafipix.modules.work.dto.response.GalleryItemPublicResponse;
import com.wafipix.wafipix.modules.work.entity.Work;
import com.wafipix.wafipix.modules.work.entity.Gallery;
import com.wafipix.wafipix.modules.work.entity.GalleryItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkMapper {

    private final ServiceMapper serviceMapper;
    private final FileMapper fileMapper;
    private final GalleryMapper galleryMapper;

    public WorkMapper(ServiceMapper serviceMapper, FileMapper fileMapper, GalleryMapper galleryMapper) {
        this.serviceMapper = serviceMapper;
        this.fileMapper = fileMapper;
        this.galleryMapper = galleryMapper;
    }

    public WorkResponse toResponse(Work work) {
        if (work == null) return null;

        return new WorkResponse(
                work.getId(),
                work.getTitle(),
                work.getSlug(),
                serviceMapper.toResponse(work.getService()),
                work.getDescription(),
                fileMapper.toResponse(work.getCoverVideo()),
                fileMapper.toResponse(work.getCoverImage()),
                fileMapper.toResponse(work.getProfileVideo()),
                fileMapper.toResponse(work.getProfileImage()),
                work.getGalleries() != null ? 
                    work.getGalleries().stream()
                        .map(galleryMapper::toResponse)
                        .toList() : List.of(),
                work.getActive(),
                work.getCreatedAt(),
                work.getUpdatedAt()
        );
    }

    public WorkListResponse toListResponse(Work work) {
        if (work == null) return null;

        return new WorkListResponse(
                work.getId(),
                work.getTitle(),
                work.getSlug(),
                serviceMapper.toResponse(work.getService()),
                work.getDescription(),
                work.getActive(),
                work.getCreatedAt(),
                work.getUpdatedAt()
        );
    }

    // Public API mapping methods
    public WorkListPublicResponse toPublicListResponse(Work work) {
        if (work == null) return null;

        return new WorkListPublicResponse(
                work.getId(),
                work.getTitle(),
                work.getSlug(),
                work.getService() != null ? work.getService().getTitle() : null,
                work.getCoverVideo() != null ? work.getCoverVideo().getPublicUrl() : null,
                work.getCoverImage() != null ? work.getCoverImage().getPublicUrl() : null,
                work.getProfileVideo() != null ? work.getProfileVideo().getPublicUrl() : null,
                work.getProfileImage() != null ? work.getProfileImage().getPublicUrl() : null
        );
    }

    public WorkDetailPublicResponse toPublicDetailResponse(Work work) {
        if (work == null) return null;

        return new WorkDetailPublicResponse(
                work.getId(),
                work.getTitle(),
                work.getSlug(),
                work.getDescription(),
                work.getCoverVideo() != null ? work.getCoverVideo().getPublicUrl() : null,
                work.getCoverImage() != null ? work.getCoverImage().getPublicUrl() : null,
                work.getProfileVideo() != null ? work.getProfileVideo().getPublicUrl() : null,
                work.getProfileImage() != null ? work.getProfileImage().getPublicUrl() : null,
                work.getGalleries() != null ? 
                    work.getGalleries().stream()
                        .map(this::toPublicGalleryResponse)
                        .toList() : List.of()
        );
    }

    public GalleryPublicResponse toPublicGalleryResponse(Gallery gallery) {
        if (gallery == null) return null;

        return new GalleryPublicResponse(
                gallery.getId(),
                gallery.getIsMobileGrid(),
                gallery.getItems() != null ? 
                    gallery.getItems().stream()
                        .map(this::toPublicGalleryItemResponse)
                        .toList() : List.of()
        );
    }

    public GalleryItemPublicResponse toPublicGalleryItemResponse(GalleryItem galleryItem) {
        if (galleryItem == null) return null;

        // Determine type based on MIME type
        String type = "image"; // default
        if (galleryItem.getFile() != null && galleryItem.getFile().getMimeType() != null) {
            String mimeType = galleryItem.getFile().getMimeType().toLowerCase();
            if (mimeType.startsWith("video/")) {
                type = "video";
            }
        }

        return new GalleryItemPublicResponse(
                galleryItem.getId(),
                type,
                galleryItem.getFile() != null ? galleryItem.getFile().getPublicUrl() : null
        );
    }
}

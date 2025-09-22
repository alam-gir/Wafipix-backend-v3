package com.wafipix.wafipix.modules.work.mapper;

import com.wafipix.wafipix.modules.filemanagement.mapper.FileMapper;
import com.wafipix.wafipix.modules.service.mapper.ServiceMapper;
import com.wafipix.wafipix.modules.work.dto.response.WorkListResponse;
import com.wafipix.wafipix.modules.work.dto.response.WorkResponse;
import com.wafipix.wafipix.modules.work.entity.Work;
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
}

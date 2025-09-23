package com.wafipix.wafipix.modules.advertisementvideo.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.advertisementvideo.dto.request.CreateAdvertisementVideoRequest;
import com.wafipix.wafipix.modules.advertisementvideo.dto.response.AdvertisementVideoResponse;
import com.wafipix.wafipix.modules.advertisementvideo.entity.AdvertisementVideo;
import com.wafipix.wafipix.modules.advertisementvideo.mapper.AdvertisementVideoMapper;
import com.wafipix.wafipix.modules.advertisementvideo.repository.AdvertisementVideoRepository;
import com.wafipix.wafipix.modules.advertisementvideo.service.AdvertisementVideoService;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementVideoServiceImpl implements AdvertisementVideoService {

    private final AdvertisementVideoRepository advertisementVideoRepository;
    private final AdvertisementVideoMapper advertisementVideoMapper;
    private final FileService fileService;

    @Override
    @Transactional
    public AdvertisementVideoResponse createOrUpdateAdvertisementVideo(CreateAdvertisementVideoRequest request) {
        log.info("Creating or updating advertisement video");

        // Validate video file
        validateVideoFile(request.getVideoFile());

        // Upload video file
        String videoUrl;
        try {
            var uploadedFile = fileService.uploadFile(request.getVideoFile(), "advertisement-videos");
            videoUrl = uploadedFile.getPublicUrl();
            log.info("Advertisement video uploaded successfully: {}", videoUrl);
        } catch (Exception e) {
            log.error("Failed to upload advertisement video: {}", e.getMessage());
            throw new BusinessException("Failed to upload advertisement video: " + e.getMessage());
        }

        // Check if advertisement video already exists
        AdvertisementVideo existingVideo = advertisementVideoRepository.findFirstByOrderByCreatedAtDesc().orElse(null);
        
        AdvertisementVideo advertisementVideo;
        if (existingVideo != null) {
            // Update existing video
            log.info("Updating existing advertisement video with ID: {}", existingVideo.getId());
            
            // Delete old video from storage
            if (existingVideo.getUrl() != null) {
                try {
                    fileService.deleteFileByUrl(existingVideo.getUrl());
                    log.info("Old advertisement video deleted: {}", existingVideo.getUrl());
                } catch (Exception e) {
                    log.error("Failed to delete old advertisement video: {}", e.getMessage());
                }
            }
            
            // Update URL
            existingVideo.setUrl(videoUrl);
            advertisementVideo = advertisementVideoRepository.save(existingVideo);
            log.info("Advertisement video updated successfully");
        } else {
            // Create new video
            log.info("Creating new advertisement video");
            advertisementVideo = advertisementVideoMapper.toEntity(request, videoUrl);
            advertisementVideo = advertisementVideoRepository.save(advertisementVideo);
            log.info("Advertisement video created successfully with ID: {}", advertisementVideo.getId());
        }

        return advertisementVideoMapper.toResponse(advertisementVideo);
    }

    @Override
    public AdvertisementVideoResponse getAdvertisementVideo() {
        log.info("Fetching advertisement video");

        AdvertisementVideo advertisementVideo = advertisementVideoRepository.findFirstByOrderByCreatedAtDesc()
                .orElse(null);

        if (advertisementVideo == null) {
            log.info("No advertisement video found");
            throw new ResourceNotFoundException("No advertisement video found");
        }

        return advertisementVideoMapper.toResponse(advertisementVideo);
    }

    @Override
    @Transactional
    public void deleteAdvertisementVideo() {
        log.info("Deleting advertisement video");

        AdvertisementVideo advertisementVideo = advertisementVideoRepository.findFirstByOrderByCreatedAtDesc()
                .orElse(null);

        if (advertisementVideo == null) {
            log.info("No advertisement video to delete");
            return;
        }

        // Delete video from file storage
        if (advertisementVideo.getUrl() != null) {
            try {
                boolean deleted = fileService.deleteFileByUrl(advertisementVideo.getUrl());
                if (deleted) {
                    log.info("Advertisement video deleted from storage: {}", advertisementVideo.getUrl());
                } else {
                    log.warn("Failed to delete advertisement video from storage: {}", advertisementVideo.getUrl());
                }
            } catch (Exception e) {
                log.error("Error deleting advertisement video from storage: {}", e.getMessage());
            }
        }

        // Delete advertisement video
        advertisementVideoRepository.delete(advertisementVideo);
        log.info("Advertisement video deleted successfully");
    }

    @Override
    public String getAdvertisementVideoUrl() {
        log.info("Fetching advertisement video URL");

        AdvertisementVideo advertisementVideo = advertisementVideoRepository.findFirstByOrderByCreatedAtDesc()
                .orElse(null);

        if (advertisementVideo == null) {
            log.info("No advertisement video found");
            throw new ResourceNotFoundException("No advertisement video found");
        }

        return advertisementVideo.getUrl();
    }

    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Video file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new BusinessException("Only video files are allowed. Received: " + contentType);
        }

        // Check file extension as additional validation
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("Invalid file name");
        }

        String extension = originalFilename.toLowerCase();
        if (!extension.endsWith(".mp4") && !extension.endsWith(".avi") && 
            !extension.endsWith(".mov") && !extension.endsWith(".wmv") && 
            !extension.endsWith(".flv") && !extension.endsWith(".webm") && 
            !extension.endsWith(".mkv") && !extension.endsWith(".m4v")) {
            throw new BusinessException("Unsupported video format. Supported formats: MP4, AVI, MOV, WMV, FLV, WebM, MKV, M4V");
        }

        log.info("Video file validation passed: {}", originalFilename);
    }
}

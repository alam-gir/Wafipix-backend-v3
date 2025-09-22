package com.wafipix.wafipix.modules.work.service.impl;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.filemanagement.entity.File;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import com.wafipix.wafipix.modules.work.dto.request.CreateGalleryRequest;
import com.wafipix.wafipix.modules.work.dto.request.UpdateGalleryRequest;
import com.wafipix.wafipix.modules.work.dto.response.GalleryResponse;
import com.wafipix.wafipix.modules.work.entity.Gallery;
import com.wafipix.wafipix.modules.work.entity.GalleryItem;
import com.wafipix.wafipix.modules.work.entity.Work;
import com.wafipix.wafipix.modules.work.mapper.GalleryMapper;
import com.wafipix.wafipix.modules.work.repository.GalleryItemRepository;
import com.wafipix.wafipix.modules.work.repository.GalleryRepository;
import com.wafipix.wafipix.modules.work.repository.WorkRepository;
import com.wafipix.wafipix.modules.work.service.GalleryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GalleryServiceImpl implements GalleryService {

    private final GalleryRepository galleryRepository;
    private final GalleryItemRepository galleryItemRepository;
    private final WorkRepository workRepository;
    private final FileService fileService;
    private final GalleryMapper galleryMapper;

    @Override
    public ApiResponse<List<GalleryResponse>> getWorkGalleries(UUID workId) {
        try {
            List<Gallery> galleries = galleryRepository.findByWorkIdWithItems(workId);
            List<GalleryResponse> response = galleries.stream()
                    .map(galleryMapper::toResponse)
                    .toList();
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("Error fetching galleries for work {}: {}", workId, e.getMessage());
            return ApiResponse.error("Failed to fetch galleries: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<GalleryResponse> createGallery(UUID workId, CreateGalleryRequest request) {
        try {
            Work work = workRepository.findById(workId)
                    .orElseThrow(() -> new RuntimeException("Work not found"));

            // Create gallery
            Gallery gallery = Gallery.builder()
                    .work(work)
                    .isMobileGrid(request.getIsMobileGrid() != null ? request.getIsMobileGrid() : false)
                    .build();

            Gallery savedGallery = galleryRepository.save(gallery);

            // Upload and add files if provided
            if (request.getFiles() != null && !request.getFiles().isEmpty()) {
                List<File> uploadedFiles = fileService.uploadFiles(request.getFiles(), "works/galleries");
                
                List<GalleryItem> galleryItems = new ArrayList<>();
                for (File file : uploadedFiles) {
                    GalleryItem item = GalleryItem.builder()
                            .gallery(savedGallery)
                            .file(file)
                            .build();
                    galleryItems.add(item);
                }
                
                galleryItemRepository.saveAll(galleryItems);
                savedGallery.setItems(galleryItems);
            }

            log.info("Gallery created successfully for work: {}", workId);
            return ApiResponse.success(galleryMapper.toResponse(savedGallery));

        } catch (Exception e) {
            log.error("Error creating gallery for work {}: {}", workId, e.getMessage());
            return ApiResponse.error("Failed to create gallery: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<GalleryResponse> updateGallery(UUID galleryId, UpdateGalleryRequest request) {
        try {
            Gallery gallery = galleryRepository.findById(galleryId)
                    .orElseThrow(() -> new RuntimeException("Gallery not found"));

            if (request.getIsMobileGrid() != null) {
                gallery.setIsMobileGrid(request.getIsMobileGrid());
            }

            Gallery savedGallery = galleryRepository.save(gallery);
            log.info("Gallery updated successfully: {}", galleryId);

            return ApiResponse.success(galleryMapper.toResponse(savedGallery));

        } catch (Exception e) {
            log.error("Error updating gallery {}: {}", galleryId, e.getMessage());
            return ApiResponse.error("Failed to update gallery: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<Void> deleteGallery(UUID galleryId) {
        try {
            Gallery gallery = galleryRepository.findByIdWithItems(galleryId)
                    .orElseThrow(() -> new RuntimeException("Gallery not found"));

            // Store file URLs before deletion
            List<String> fileUrls = gallery.getItems().stream()
                    .map(item -> item.getFile().getPublicUrl())
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .toList();

            // Delete gallery first
            galleryRepository.delete(gallery);
            log.info("Gallery deleted successfully: {}", galleryId);

            // Clean up files after gallery deletion (outside transaction)
            for (String fileUrl : fileUrls) {
                try {
                    fileService.deleteFileByUrl(fileUrl);
                    log.info("File deleted successfully: {}", fileUrl);
                } catch (Exception e) {
                    log.error("Error deleting file {}: {}", fileUrl, e.getMessage());
                    // Continue with other files even if one fails
                }
            }

            return ApiResponse.success(null);

        } catch (Exception e) {
            log.error("Error deleting gallery {}: {}", galleryId, e.getMessage());
            return ApiResponse.error("Failed to delete gallery: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<GalleryResponse> addFilesToGallery(UUID galleryId, List<MultipartFile> files) {
        try {
            Gallery gallery = galleryRepository.findByIdWithItems(galleryId)
                    .orElseThrow(() -> new RuntimeException("Gallery not found"));

            if (files == null || files.isEmpty()) {
                return ApiResponse.error("No files provided", 400);
            }

            // Upload files
            List<File> uploadedFiles = fileService.uploadFiles(files, "works/galleries");
            
            // Create gallery items
            List<GalleryItem> newItems = new ArrayList<>();
            for (File file : uploadedFiles) {
                GalleryItem item = GalleryItem.builder()
                        .gallery(gallery)
                        .file(file)
                        .build();
                newItems.add(item);
            }
            
            List<GalleryItem> savedItems = galleryItemRepository.saveAll(newItems);
            
            // Update gallery items list
            List<GalleryItem> existingItems = gallery.getItems();
            existingItems.addAll(savedItems);
            gallery.setItems(existingItems);

            log.info("Added {} files to gallery: {}", uploadedFiles.size(), galleryId);
            return ApiResponse.success(galleryMapper.toResponse(gallery));

        } catch (Exception e) {
            log.error("Error adding files to gallery {}: {}", galleryId, e.getMessage());
            return ApiResponse.error("Failed to add files to gallery: " + e.getMessage(), 500);
        }
    }

    @Override
    @Transactional
    public ApiResponse<GalleryResponse> removeFilesFromGallery(UUID galleryId, List<UUID> galleryItemIds) {
        try {
            if (galleryItemIds == null || galleryItemIds.isEmpty()) {
                return ApiResponse.error("No gallery item IDs provided", 400);
            }

            // Verify gallery exists
            if (!galleryRepository.existsById(galleryId)) {
                return ApiResponse.error("Gallery not found", 404);
            }

            // Find gallery items to delete by their IDs
            List<GalleryItem> itemsToDelete = galleryItemRepository.findAllById(galleryItemIds)
                    .stream()
                    .filter(item -> item.getGallery().getId().equals(galleryId)) // Ensure items belong to this gallery
                    .toList();

            if (itemsToDelete.isEmpty()) {
                return ApiResponse.error("No valid gallery items found to delete", 400);
            }

            // Store file URLs before deletion
            List<String> fileUrls = itemsToDelete.stream()
                    .map(item -> item.getFile().getPublicUrl())
                    .filter(url -> url != null && !url.trim().isEmpty())
                    .toList();

            // Delete gallery items by their IDs
            galleryItemRepository.deleteAllById(galleryItemIds);

            log.info("Removed {} gallery items from gallery: {}", itemsToDelete.size(), galleryId);
            
            // Clean up files after deletion (outside transaction)
            for (String fileUrl : fileUrls) {
                try {
                    fileService.deleteFileByUrl(fileUrl);
                    log.info("File deleted successfully: {}", fileUrl);
                } catch (Exception e) {
                    log.error("Error deleting file {}: {}", fileUrl, e.getMessage());
                    // Continue with other files even if one fails
                }
            }

            // Fetch updated gallery with remaining items
            Gallery updatedGallery = galleryRepository.findByIdWithItems(galleryId)
                    .orElseThrow(() -> new RuntimeException("Gallery not found after deletion"));
            
            return ApiResponse.success(galleryMapper.toResponse(updatedGallery));

        } catch (Exception e) {
            log.error("Error removing files from gallery {}: {}", galleryId, e.getMessage());
            return ApiResponse.error("Failed to remove files from gallery: " + e.getMessage(), 500);
        }
    }
}

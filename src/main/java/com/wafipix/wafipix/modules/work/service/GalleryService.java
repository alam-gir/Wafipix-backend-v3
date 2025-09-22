package com.wafipix.wafipix.modules.work.service;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.work.dto.request.CreateGalleryRequest;
import com.wafipix.wafipix.modules.work.dto.request.UpdateGalleryRequest;
import com.wafipix.wafipix.modules.work.dto.response.GalleryResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface GalleryService {

    ApiResponse<List<GalleryResponse>> getWorkGalleries(UUID workId);

    ApiResponse<GalleryResponse> createGallery(UUID workId, CreateGalleryRequest request);

    ApiResponse<GalleryResponse> updateGallery(UUID galleryId, UpdateGalleryRequest request);

    ApiResponse<Void> deleteGallery(UUID galleryId);

    ApiResponse<GalleryResponse> addFilesToGallery(UUID galleryId, List<MultipartFile> files);

    ApiResponse<GalleryResponse> removeFilesFromGallery(UUID galleryId, List<UUID> galleryItemIds);
}

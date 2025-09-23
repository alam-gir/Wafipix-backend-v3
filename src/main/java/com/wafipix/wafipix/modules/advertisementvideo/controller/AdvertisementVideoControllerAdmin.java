package com.wafipix.wafipix.modules.advertisementvideo.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.advertisementvideo.dto.request.CreateAdvertisementVideoRequest;
import com.wafipix.wafipix.modules.advertisementvideo.dto.response.AdvertisementVideoResponse;
import com.wafipix.wafipix.modules.advertisementvideo.service.AdvertisementVideoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v3/admin/advertisement-videos")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdvertisementVideoControllerAdmin {

    private final AdvertisementVideoService advertisementVideoService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<AdvertisementVideoResponse>> createOrUpdateAdvertisementVideo(
            @Valid @ModelAttribute CreateAdvertisementVideoRequest request
    ) {
        log.info("Creating or updating advertisement video");
        AdvertisementVideoResponse response = advertisementVideoService.createOrUpdateAdvertisementVideo(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AdvertisementVideoResponse>> getAdvertisementVideo() {
        log.info("Fetching advertisement video");
        AdvertisementVideoResponse response = advertisementVideoService.getAdvertisementVideo();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAdvertisementVideo() {
        log.info("Deleting advertisement video");
        advertisementVideoService.deleteAdvertisementVideo();
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.NO_CONTENT);
    }
}

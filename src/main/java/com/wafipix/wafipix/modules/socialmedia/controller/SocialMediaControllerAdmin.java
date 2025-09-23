package com.wafipix.wafipix.modules.socialmedia.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.socialmedia.dto.request.CreateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.request.UpdateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaListResponse;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaResponse;
import com.wafipix.wafipix.modules.socialmedia.service.SocialMediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/admin/social-media")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class SocialMediaControllerAdmin {

    private final SocialMediaService socialMediaService;

    @PostMapping
    public ResponseEntity<ApiResponse<SocialMediaResponse>> createSocialMedia(
            @Valid @RequestBody CreateSocialMediaRequest request
    ) {
        log.info("Creating social media with title: {}", request.getTitle());
        SocialMediaResponse response = socialMediaService.createSocialMedia(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SocialMediaResponse>>> getAllSocialMedia() {
        log.info("Fetching all social media");
        List<SocialMediaResponse> response = socialMediaService.getAllSocialMedia();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<SocialMediaListResponse>>> getAllSocialMediaPaginated(Pageable pageable) {
        log.info("Fetching social media with pagination");
        Page<SocialMediaListResponse> response = socialMediaService.getAllSocialMedia(pageable);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SocialMediaResponse>> getSocialMediaById(@PathVariable UUID id) {
        log.info("Fetching social media with ID: {}", id);
        SocialMediaResponse response = socialMediaService.getSocialMediaById(id);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SocialMediaResponse>> updateSocialMedia(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSocialMediaRequest request
    ) {
        log.info("Updating social media with ID: {}", id);
        SocialMediaResponse response = socialMediaService.updateSocialMedia(id, request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSocialMedia(@PathVariable UUID id) {
        log.info("Deleting social media with ID: {}", id);
        socialMediaService.deleteSocialMedia(id);
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.NO_CONTENT);
    }
}

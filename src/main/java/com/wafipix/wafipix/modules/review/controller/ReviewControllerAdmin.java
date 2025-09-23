package com.wafipix.wafipix.modules.review.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.review.dto.request.CreateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.request.UpdateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.response.ReviewListResponse;
import com.wafipix.wafipix.modules.review.dto.response.ReviewResponse;
import com.wafipix.wafipix.modules.review.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v3/admin/reviews")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class ReviewControllerAdmin {

    private final ReviewService reviewService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @Valid @ModelAttribute CreateReviewRequest request
    ) {
        log.info("Creating review for platform: {}", request.getPlatform());
        ReviewResponse response = reviewService.createReview(request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews() {
        log.info("Fetching all reviews");
        List<ReviewResponse> response = reviewService.getAllReviews();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<Page<ReviewListResponse>>> getAllReviewsPaginated(Pageable pageable) {
        log.info("Fetching reviews with pagination");
        Page<ReviewListResponse> response = reviewService.getAllReviews(pageable);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/platforms")
    public ResponseEntity<ApiResponse<List<String>>> getActivePlatforms() {
        log.info("Fetching active platforms");
        List<String> response = reviewService.getActivePlatforms();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(@PathVariable UUID id) {
        log.info("Fetching review with ID: {}", id);
        ReviewResponse response = reviewService.getReviewById(id);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable UUID id,
            @Valid @ModelAttribute UpdateReviewRequest request
    ) {
        log.info("Updating review with ID: {}", id);
        ReviewResponse response = reviewService.updateReview(id, request);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable UUID id) {
        log.info("Deleting review with ID: {}", id);
        reviewService.deleteReview(id);
        return new ResponseEntity<>(ApiResponse.success(null), HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/activity-status")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReviewActivityStatus(
            @PathVariable UUID id,
            @RequestParam @NotNull Boolean active
    ) {
        log.info("Updating activity status for review with ID: {} to {}", id, active);
        ReviewResponse response = reviewService.updateReviewActivityStatus(id, active);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }
}

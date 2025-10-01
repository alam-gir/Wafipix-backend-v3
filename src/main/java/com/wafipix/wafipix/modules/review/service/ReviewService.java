package com.wafipix.wafipix.modules.review.service;

import com.wafipix.wafipix.modules.review.dto.request.CreateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.request.UpdateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.response.ReviewListResponse;
import com.wafipix.wafipix.modules.review.dto.response.ReviewResponse;
import com.wafipix.wafipix.modules.review.dto.response.ReviewResponsePublic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    ReviewResponse createReview(CreateReviewRequest request);
    List<ReviewResponse> getAllReviews();
    Page<ReviewListResponse> getAllReviews(Pageable pageable);
    List<ReviewResponse> getActiveReviews();
    List<ReviewResponse> getActiveReviewsByPlatform(String platform);
    List<String> getActivePlatforms();
    
    // Public API methods
    List<ReviewResponsePublic> getPublicActiveReviews();
    List<ReviewResponsePublic> getPublicActiveReviewsByPlatform(String platform);
    ReviewResponse getReviewById(UUID id);
    ReviewResponse updateReview(UUID id, UpdateReviewRequest request);
    void deleteReview(UUID id);
    ReviewResponse updateReviewActivityStatus(UUID id, Boolean active);
}

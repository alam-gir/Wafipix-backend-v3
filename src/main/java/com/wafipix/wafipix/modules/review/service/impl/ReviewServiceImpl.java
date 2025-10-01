package com.wafipix.wafipix.modules.review.service.impl;

import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.review.dto.request.CreateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.request.UpdateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.response.ReviewListResponse;
import com.wafipix.wafipix.modules.review.dto.response.ReviewResponse;
import com.wafipix.wafipix.modules.review.dto.response.ReviewResponsePublic;
import com.wafipix.wafipix.modules.review.entity.Review;
import com.wafipix.wafipix.modules.review.mapper.ReviewMapper;
import com.wafipix.wafipix.modules.review.repository.ReviewRepository;
import com.wafipix.wafipix.modules.review.service.ReviewService;
import com.wafipix.wafipix.modules.filemanagement.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final FileService fileService;

    @Override
    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        log.info("Creating review for platform: {}", request.getPlatform());

        // Upload review image if provided
        String reviewImageUrl = null;
        if (request.getReviewImage() != null && !request.getReviewImage().isEmpty()) {
            try {
                var uploadedFile = fileService.uploadFile(request.getReviewImage(), "reviews/images");
                reviewImageUrl = uploadedFile.getPublicUrl();
                log.info("Review image uploaded successfully: {}", reviewImageUrl);
            } catch (Exception e) {
                log.error("Failed to upload review image: {}", e.getMessage());
                throw new RuntimeException("Failed to upload review image: " + e.getMessage());
            }
        } else {
            log.info("No review image provided, creating review without image");
        }

        // Create review entity
        Review review = reviewMapper.toEntity(request, reviewImageUrl);
        // Convert platform to lowercase to avoid duplicates
        review.setPlatform(review.getPlatform().toLowerCase());
        Review savedReview = reviewRepository.save(review);

        log.info("Review created successfully with ID: {}", savedReview.getId());
        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        log.info("Fetching all reviews");

        List<Review> reviews = reviewRepository.findAll();
        log.info("Found {} reviews", reviews.size());

        return reviewMapper.toResponseList(reviews);
    }

    @Override
    public Page<ReviewListResponse> getAllReviews(Pageable pageable) {
        log.info("Fetching reviews with pagination");

        Page<Review> reviewPage = reviewRepository.findAllOrdered(pageable);
        Page<ReviewListResponse> response = reviewPage.map(reviewMapper::toListResponse);

        log.info("Found {} reviews", reviewPage.getTotalElements());
        return response;
    }

    @Override
    public List<ReviewResponse> getActiveReviews() {
        log.info("Fetching active reviews (shuffled)");

        List<Review> reviews = reviewRepository.findActiveReviewsRandom();
        log.info("Found {} active reviews", reviews.size());

        return reviewMapper.toResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getActiveReviewsByPlatform(String platform) {
        log.info("Fetching active reviews for platform: {}", platform);

        List<Review> reviews = reviewRepository.findActiveReviewsByPlatform(platform);
        log.info("Found {} active reviews for platform: {}", reviews.size(), platform);

        return reviewMapper.toResponseList(reviews);
    }

    @Override
    public List<String> getActivePlatforms() {
        log.info("Fetching active platforms");

        List<String> platforms = reviewRepository.findDistinctActivePlatforms();
        log.info("Found {} active platforms", platforms.size());

        return platforms;
    }

    @Override
    public ReviewResponse getReviewById(UUID id) {
        log.info("Fetching review with ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));

        return reviewMapper.toResponse(review);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(UUID id, UpdateReviewRequest request) {
        log.info("Updating review with ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));

        // Handle review image update if provided
        String reviewImageUrl = null;
        if (request.getReviewImage() != null && !request.getReviewImage().isEmpty()) {
            try {
                // Delete old review image if exists
                if (review.getReviewImage() != null) {
                    fileService.deleteFileByUrl(review.getReviewImage());
                    log.info("Old review image deleted: {}", review.getReviewImage());
                }

                // Upload new review image
                var uploadedFile = fileService.uploadFile(request.getReviewImage(), "reviews/images");
                reviewImageUrl = uploadedFile.getPublicUrl();
                log.info("New review image uploaded successfully: {}", reviewImageUrl);
            } catch (Exception e) {
                log.error("Failed to update review image: {}", e.getMessage());
                throw new RuntimeException("Failed to update review image: " + e.getMessage());
            }
        }

        // Update review
        reviewMapper.updateEntity(review, request, reviewImageUrl);
        // Convert platform to lowercase if updated
        if (request.getPlatform() != null && !request.getPlatform().trim().isEmpty()) {
            review.setPlatform(review.getPlatform().toLowerCase());
        }
        Review updatedReview = reviewRepository.save(review);

        log.info("Review updated successfully with ID: {}", updatedReview.getId());
        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(UUID id) {
        log.info("Deleting review with ID: {}", id);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));

        // Delete review image from file storage
        if (review.getReviewImage() != null) {
            try {
                boolean deleted = fileService.deleteFileByUrl(review.getReviewImage());
                if (deleted) {
                    log.info("Review image deleted from storage: {}", review.getReviewImage());
                } else {
                    log.warn("Failed to delete review image from storage: {}", review.getReviewImage());
                }
            } catch (Exception e) {
                log.error("Error deleting review image from storage: {}", e.getMessage());
                // Continue with review deletion even if image deletion fails
            }
        }

        // Delete review
        reviewRepository.delete(review);

        log.info("Review deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional
    public ReviewResponse updateReviewActivityStatus(UUID id, Boolean active) {
        log.info("Updating activity status for review with ID: {} to {}", id, active);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with ID: " + id));

        review.setActive(active);
        Review updatedReview = reviewRepository.save(review);

        log.info("Review activity status updated for ID: {} to {}", id, active);
        return reviewMapper.toResponse(updatedReview);
    }

    // Public API implementations
    @Override
    public List<ReviewResponsePublic> getPublicActiveReviews() {
        log.info("Fetching active reviews for public display (shuffled)");

        List<Review> reviews = reviewRepository.findActiveReviewsRandom();
        log.info("Found {} active reviews for public display", reviews.size());

        return reviewMapper.toPublicResponseList(reviews);
    }

    @Override
    public List<ReviewResponsePublic> getPublicActiveReviewsByPlatform(String platform) {
        log.info("Fetching active reviews for platform: {} for public display", platform);

        List<Review> reviews = reviewRepository.findActiveReviewsByPlatform(platform);
        log.info("Found {} active reviews for platform: {} for public display", reviews.size(), platform);

        return reviewMapper.toPublicResponseList(reviews);
    }
}

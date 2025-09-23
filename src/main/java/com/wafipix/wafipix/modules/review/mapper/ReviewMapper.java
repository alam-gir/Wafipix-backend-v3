package com.wafipix.wafipix.modules.review.mapper;

import com.wafipix.wafipix.modules.review.dto.request.CreateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.request.UpdateReviewRequest;
import com.wafipix.wafipix.modules.review.dto.response.ReviewListResponse;
import com.wafipix.wafipix.modules.review.dto.response.ReviewResponse;
import com.wafipix.wafipix.modules.review.entity.Review;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        if (review == null) return null;

        return new ReviewResponse(
                review.getId(),
                review.getReviewImage(),
                review.getPlatform(),
                review.getClientName(),
                review.getRating(),
                review.getReviewText(),
                review.getActive(),
                review.getCreatedAt(),
                review.getUpdatedAt(),
                review.getCreatedBy(),
                review.getUpdatedBy()
        );
    }

    public ReviewListResponse toListResponse(Review review) {
        if (review == null) return null;

        return new ReviewListResponse(
                review.getId(),
                review.getReviewImage(),
                review.getPlatform(),
                review.getClientName(),
                review.getRating(),
                review.getReviewText(),
                review.getActive(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    public List<ReviewResponse> toResponseList(List<Review> reviews) {
        if (reviews == null) return List.of();
        return reviews.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ReviewListResponse> toListResponseList(List<Review> reviews) {
        if (reviews == null) return List.of();
        return reviews.stream()
                .map(this::toListResponse)
                .toList();
    }

    public Review toEntity(CreateReviewRequest request, String reviewImageUrl) {
        if (request == null) return null;

        return Review.builder()
                .platform(request.getPlatform())
                .clientName(request.getClientName())
                .rating(request.getRating())
                .reviewText(request.getReviewText())
                .reviewImage(reviewImageUrl)
                .active(true)
                .build();
    }

    public void updateEntity(Review review, UpdateReviewRequest request, String reviewImageUrl) {
        if (review == null || request == null) return;

        if (request.getPlatform() != null && !request.getPlatform().trim().isEmpty()) {
            review.setPlatform(request.getPlatform());
        }

        if (request.getClientName() != null) {
            review.setClientName(request.getClientName());
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        if (request.getReviewText() != null) {
            review.setReviewText(request.getReviewText());
        }

        if (reviewImageUrl != null) {
            review.setReviewImage(reviewImageUrl);
        }
    }
}

package com.wafipix.wafipix.modules.review.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.review.dto.response.ReviewResponsePublic;
import com.wafipix.wafipix.modules.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v3/public/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewPublicController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponsePublic>>> getActiveReviews() {
        log.info("Fetching active reviews for public display (shuffled)");
        List<ReviewResponsePublic> response = reviewService.getPublicActiveReviews();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/platforms")
    public ResponseEntity<ApiResponse<List<String>>> getActivePlatforms() {
        log.info("Fetching active platforms for public display");
        List<String> response = reviewService.getActivePlatforms();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }

    @GetMapping("/platform/{platform}")
    public ResponseEntity<ApiResponse<List<ReviewResponsePublic>>> getActiveReviewsByPlatform(
            @PathVariable String platform
    ) {
        log.info("Fetching active reviews for platform: {} (shuffled)", platform);
        List<ReviewResponsePublic> response = reviewService.getPublicActiveReviewsByPlatform(platform);
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }
}

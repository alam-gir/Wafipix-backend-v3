package com.wafipix.wafipix.modules.socialmedia.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaPublicResponse;
import com.wafipix.wafipix.modules.socialmedia.service.SocialMediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v3/public/social-media")
@RequiredArgsConstructor
@Slf4j
public class SocialMediaPublicController {

    private final SocialMediaService socialMediaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SocialMediaPublicResponse>>> getAllSocialMedia() {
        log.info("Fetching all social media for public display");
        List<SocialMediaPublicResponse> response = socialMediaService.getPublicSocialMedia();
        return new ResponseEntity<>(ApiResponse.success(response), HttpStatus.OK);
    }
}

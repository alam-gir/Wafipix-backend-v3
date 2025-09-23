package com.wafipix.wafipix.modules.advertisementvideo.controller;

import com.wafipix.wafipix.common.dto.ApiResponse;
import com.wafipix.wafipix.modules.advertisementvideo.service.AdvertisementVideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v3/public/advertisement-videos")
@RequiredArgsConstructor
@Slf4j
public class AdvertisementVideoPublicController {

    private final AdvertisementVideoService advertisementVideoService;

    @GetMapping
    public ResponseEntity<ApiResponse<String>> getAdvertisementVideoUrl() {
        log.info("Fetching advertisement video URL for public display");
        String videoUrl = advertisementVideoService.getAdvertisementVideoUrl();
        return new ResponseEntity<>(ApiResponse.success(videoUrl), HttpStatus.OK);
    }
}

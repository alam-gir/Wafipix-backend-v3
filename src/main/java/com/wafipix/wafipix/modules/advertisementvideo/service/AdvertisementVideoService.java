package com.wafipix.wafipix.modules.advertisementvideo.service;

import com.wafipix.wafipix.modules.advertisementvideo.dto.request.CreateAdvertisementVideoRequest;
import com.wafipix.wafipix.modules.advertisementvideo.dto.response.AdvertisementVideoResponse;

public interface AdvertisementVideoService {
    AdvertisementVideoResponse createOrUpdateAdvertisementVideo(CreateAdvertisementVideoRequest request);
    AdvertisementVideoResponse getAdvertisementVideo();
    void deleteAdvertisementVideo();
    String getAdvertisementVideoUrl();
}

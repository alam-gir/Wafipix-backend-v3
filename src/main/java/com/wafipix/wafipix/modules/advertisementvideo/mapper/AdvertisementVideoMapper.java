package com.wafipix.wafipix.modules.advertisementvideo.mapper;

import com.wafipix.wafipix.modules.advertisementvideo.dto.request.CreateAdvertisementVideoRequest;
import com.wafipix.wafipix.modules.advertisementvideo.dto.response.AdvertisementVideoResponse;
import com.wafipix.wafipix.modules.advertisementvideo.entity.AdvertisementVideo;
import org.springframework.stereotype.Component;

@Component
public class AdvertisementVideoMapper {

    public AdvertisementVideoResponse toResponse(AdvertisementVideo advertisementVideo) {
        if (advertisementVideo == null) return null;

        return new AdvertisementVideoResponse(
                advertisementVideo.getId(),
                advertisementVideo.getUrl(),
                advertisementVideo.getCreatedAt(),
                advertisementVideo.getUpdatedAt(),
                advertisementVideo.getCreatedBy(),
                advertisementVideo.getUpdatedBy()
        );
    }

    public AdvertisementVideo toEntity(CreateAdvertisementVideoRequest request, String videoUrl) {
        if (request == null) return null;

        return AdvertisementVideo.builder()
                .url(videoUrl)
                .build();
    }
}

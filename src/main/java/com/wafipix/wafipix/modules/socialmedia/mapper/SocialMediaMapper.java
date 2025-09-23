package com.wafipix.wafipix.modules.socialmedia.mapper;

import com.wafipix.wafipix.modules.socialmedia.dto.request.CreateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.request.UpdateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaListResponse;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaResponse;
import com.wafipix.wafipix.modules.socialmedia.entity.SocialMedia;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SocialMediaMapper {

    public SocialMediaResponse toResponse(SocialMedia socialMedia) {
        if (socialMedia == null) return null;

        return new SocialMediaResponse(
                socialMedia.getId(),
                socialMedia.getTitle(),
                socialMedia.getUrl(),
                socialMedia.getCreatedAt(),
                socialMedia.getUpdatedAt(),
                socialMedia.getCreatedBy(),
                socialMedia.getUpdatedBy()
        );
    }

    public SocialMediaListResponse toListResponse(SocialMedia socialMedia) {
        if (socialMedia == null) return null;

        return new SocialMediaListResponse(
                socialMedia.getId(),
                socialMedia.getTitle(),
                socialMedia.getUrl(),
                socialMedia.getCreatedAt(),
                socialMedia.getUpdatedAt()
        );
    }

    public List<SocialMediaResponse> toResponseList(List<SocialMedia> socialMediaList) {
        if (socialMediaList == null) return List.of();
        return socialMediaList.stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SocialMediaListResponse> toListResponseList(List<SocialMedia> socialMediaList) {
        if (socialMediaList == null) return List.of();
        return socialMediaList.stream()
                .map(this::toListResponse)
                .toList();
    }

    public SocialMedia toEntity(CreateSocialMediaRequest request) {
        if (request == null) return null;

        return SocialMedia.builder()
                .title(request.getTitle())
                .url(request.getUrl())
                .build();
    }

    public void updateEntity(SocialMedia socialMedia, UpdateSocialMediaRequest request) {
        if (socialMedia == null || request == null) return;

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            socialMedia.setTitle(request.getTitle());
        }

        if (request.getUrl() != null && !request.getUrl().trim().isEmpty()) {
            socialMedia.setUrl(request.getUrl());
        }
    }
}

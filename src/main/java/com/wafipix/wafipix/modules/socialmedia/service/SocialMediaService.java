package com.wafipix.wafipix.modules.socialmedia.service;

import com.wafipix.wafipix.modules.socialmedia.dto.request.CreateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.request.UpdateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaListResponse;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface SocialMediaService {
    SocialMediaResponse createSocialMedia(CreateSocialMediaRequest request);
    List<SocialMediaResponse> getAllSocialMedia();
    Page<SocialMediaListResponse> getAllSocialMedia(Pageable pageable);
    SocialMediaResponse getSocialMediaById(UUID id);
    SocialMediaResponse updateSocialMedia(UUID id, UpdateSocialMediaRequest request);
    void deleteSocialMedia(UUID id);
}

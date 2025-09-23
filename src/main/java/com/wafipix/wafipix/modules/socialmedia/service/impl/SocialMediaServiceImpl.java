package com.wafipix.wafipix.modules.socialmedia.service.impl;

import com.wafipix.wafipix.common.exception.ResourceNotFoundException;
import com.wafipix.wafipix.modules.socialmedia.dto.request.CreateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.request.UpdateSocialMediaRequest;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaListResponse;
import com.wafipix.wafipix.modules.socialmedia.dto.response.SocialMediaResponse;
import com.wafipix.wafipix.modules.socialmedia.entity.SocialMedia;
import com.wafipix.wafipix.modules.socialmedia.mapper.SocialMediaMapper;
import com.wafipix.wafipix.modules.socialmedia.repository.SocialMediaRepository;
import com.wafipix.wafipix.modules.socialmedia.service.SocialMediaService;
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
public class SocialMediaServiceImpl implements SocialMediaService {

    private final SocialMediaRepository socialMediaRepository;
    private final SocialMediaMapper socialMediaMapper;

    @Override
    @Transactional
    public SocialMediaResponse createSocialMedia(CreateSocialMediaRequest request) {
        log.info("Creating social media with title: {}", request.getTitle());

        SocialMedia socialMedia = socialMediaMapper.toEntity(request);
        SocialMedia savedSocialMedia = socialMediaRepository.save(socialMedia);

        log.info("Social media created successfully with ID: {}", savedSocialMedia.getId());
        return socialMediaMapper.toResponse(savedSocialMedia);
    }

    @Override
    public List<SocialMediaResponse> getAllSocialMedia() {
        log.info("Fetching all social media");

        List<SocialMedia> socialMediaList = socialMediaRepository.findAllOrdered();
        log.info("Found {} social media", socialMediaList.size());

        return socialMediaMapper.toResponseList(socialMediaList);
    }

    @Override
    public Page<SocialMediaListResponse> getAllSocialMedia(Pageable pageable) {
        log.info("Fetching social media with pagination");

        Page<SocialMedia> socialMediaPage = socialMediaRepository.findAllOrdered(pageable);
        Page<SocialMediaListResponse> response = socialMediaPage.map(socialMediaMapper::toListResponse);

        log.info("Found {} social media", socialMediaPage.getTotalElements());
        return response;
    }

    @Override
    public SocialMediaResponse getSocialMediaById(UUID id) {
        log.info("Fetching social media with ID: {}", id);

        SocialMedia socialMedia = socialMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social media not found with ID: " + id));

        return socialMediaMapper.toResponse(socialMedia);
    }

    @Override
    @Transactional
    public SocialMediaResponse updateSocialMedia(UUID id, UpdateSocialMediaRequest request) {
        log.info("Updating social media with ID: {}", id);

        SocialMedia socialMedia = socialMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social media not found with ID: " + id));

        socialMediaMapper.updateEntity(socialMedia, request);
        SocialMedia updatedSocialMedia = socialMediaRepository.save(socialMedia);

        log.info("Social media updated successfully with ID: {}", updatedSocialMedia.getId());
        return socialMediaMapper.toResponse(updatedSocialMedia);
    }

    @Override
    @Transactional
    public void deleteSocialMedia(UUID id) {
        log.info("Deleting social media with ID: {}", id);

        SocialMedia socialMedia = socialMediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Social media not found with ID: " + id));

        socialMediaRepository.delete(socialMedia);

        log.info("Social media deleted successfully with ID: {}", id);
    }
}

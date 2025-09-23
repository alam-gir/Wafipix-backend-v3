package com.wafipix.wafipix.modules.advertisementvideo.repository;

import com.wafipix.wafipix.modules.advertisementvideo.entity.AdvertisementVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdvertisementVideoRepository extends JpaRepository<AdvertisementVideo, UUID> {

    Optional<AdvertisementVideo> findFirstByOrderByCreatedAtDesc();
}

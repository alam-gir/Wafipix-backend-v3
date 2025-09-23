package com.wafipix.wafipix.modules.socialmedia.repository;

import com.wafipix.wafipix.modules.socialmedia.entity.SocialMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SocialMediaRepository extends JpaRepository<SocialMedia, UUID> {

    @Query("SELECT sm FROM SocialMedia sm ORDER BY sm.createdAt DESC")
    Page<SocialMedia> findAllOrdered(Pageable pageable);

    @Query("SELECT sm FROM SocialMedia sm ORDER BY sm.createdAt DESC")
    List<SocialMedia> findAllOrdered();
}

package com.wafipix.wafipix.modules.review.repository;

import com.wafipix.wafipix.modules.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    @Query("SELECT r FROM Review r WHERE r.active = true ORDER BY RANDOM()")
    List<Review> findActiveReviewsRandom();

    @Query("SELECT r FROM Review r WHERE r.active = true AND r.platform = :platform ORDER BY RANDOM()")
    List<Review> findActiveReviewsByPlatform(@Param("platform") String platform);

    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    Page<Review> findAllOrdered(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.platform = :platform ORDER BY r.createdAt DESC")
    Page<Review> findByPlatform(@Param("platform") String platform, Pageable pageable);

    @Query("SELECT DISTINCT r.platform FROM Review r WHERE r.active = true ORDER BY r.platform")
    List<String> findDistinctActivePlatforms();
}

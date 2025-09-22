package com.wafipix.wafipix.modules.work.repository;

import com.wafipix.wafipix.modules.work.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, UUID> {

    List<Gallery> findByWorkId(UUID workId);

    @Query("SELECT g FROM Gallery g " +
           "LEFT JOIN FETCH g.items gi " +
           "LEFT JOIN FETCH gi.file " +
           "WHERE g.work.id = :workId")
    List<Gallery> findByWorkIdWithItems(@Param("workId") UUID workId);

    @Query("SELECT g FROM Gallery g " +
           "LEFT JOIN FETCH g.items gi " +
           "LEFT JOIN FETCH gi.file " +
           "WHERE g.id = :galleryId")
    Optional<Gallery> findByIdWithItems(@Param("galleryId") UUID galleryId);
}

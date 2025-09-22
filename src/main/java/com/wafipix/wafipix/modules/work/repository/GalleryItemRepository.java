package com.wafipix.wafipix.modules.work.repository;

import com.wafipix.wafipix.modules.work.entity.GalleryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GalleryItemRepository extends JpaRepository<GalleryItem, UUID> {

    List<GalleryItem> findByGalleryId(UUID galleryId);

    @Query("SELECT gi FROM GalleryItem gi " +
           "LEFT JOIN FETCH gi.file " +
           "WHERE gi.gallery.id = :galleryId")
    List<GalleryItem> findByGalleryIdWithFile(@Param("galleryId") UUID galleryId);

    boolean existsByGalleryIdAndFileId(UUID galleryId, UUID fileId);
}

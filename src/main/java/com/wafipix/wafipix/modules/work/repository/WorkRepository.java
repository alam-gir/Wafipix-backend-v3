package com.wafipix.wafipix.modules.work.repository;

import com.wafipix.wafipix.modules.work.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {

    Optional<Work> findBySlug(String slug);

    @Query("SELECT w FROM Work w " +
           "LEFT JOIN FETCH w.coverVideo " +
           "LEFT JOIN FETCH w.coverImage " +
           "LEFT JOIN FETCH w.profileVideo " +
           "LEFT JOIN FETCH w.profileImage " +
           "LEFT JOIN FETCH w.service " +
           "WHERE w.id = :id")
    Optional<Work> findByIdWithFiles(@Param("id") UUID id);

    @Query("SELECT w FROM Work w " +
           "LEFT JOIN FETCH w.coverVideo " +
           "LEFT JOIN FETCH w.coverImage " +
           "LEFT JOIN FETCH w.profileVideo " +
           "LEFT JOIN FETCH w.profileImage " +
           "LEFT JOIN FETCH w.service " +
           "WHERE w.slug = :slug")
    Optional<Work> findBySlugWithFiles(@Param("slug") String slug);

    @Query("SELECT w FROM Work w " +
           "LEFT JOIN FETCH w.service " +
           "WHERE w.active = :active")
    Page<Work> findAllByActive(@Param("active") Boolean active, Pageable pageable);

    @Query("SELECT w FROM Work w " +
           "LEFT JOIN FETCH w.service " +
           "WHERE w.service.id = :serviceId")
    Page<Work> findAllByServiceId(@Param("serviceId") UUID serviceId, Pageable pageable);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, UUID id);
}

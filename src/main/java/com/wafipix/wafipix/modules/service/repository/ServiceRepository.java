package com.wafipix.wafipix.modules.service.repository;

import com.wafipix.wafipix.modules.service.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceRepository extends JpaRepository<Service, UUID> {
    
    boolean existsByTitleIgnoreCase(String title);
    
    boolean existsByTitleIgnoreCaseAndIdNot(String title, UUID id);
    
    boolean existsBySlugIgnoreCase(String slug);
    
    boolean existsBySlugIgnoreCaseAndIdNot(String slug, UUID id);
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category WHERE s.id = :id")
    Optional<Service> findByIdWithCategory(@Param("id") UUID id);
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.packages WHERE s.id = :id")
    Optional<Service> findByIdWithCategoryAndPackages(@Param("id") UUID id);
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category WHERE s.slug = :slug AND s.active = true")
    Optional<Service> findBySlugWithCategory(@Param("slug") String slug);
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.packages WHERE s.slug = :slug AND s.active = true")
    Optional<Service> findBySlugWithCategoryAndPackages(@Param("slug") String slug);
    
    // Search and pagination queries
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category WHERE " +
           "(:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:slug IS NULL OR LOWER(s.slug) LIKE LOWER(CONCAT('%', :slug, '%'))) AND " +
           "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
           "(:active IS NULL OR s.active = :active)")
    Page<Service> findServicesWithFilters(@Param("title") String title, 
                                        @Param("slug") String slug,
                                        @Param("categoryId") UUID categoryId,
                                        @Param("active") Boolean active,
                                        Pageable pageable);
    
    
    // Public APIs - only active services
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category WHERE s.active = true")
    List<Service> findActiveServices();
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category WHERE s.active = true AND s.category.id = :categoryId")
    List<Service> findActiveServicesByCategory(@Param("categoryId") UUID categoryId);
}

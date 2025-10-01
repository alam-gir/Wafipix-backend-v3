package com.wafipix.wafipix.modules.service.repository;

import com.wafipix.wafipix.modules.service.entity.FAQ;
import com.wafipix.wafipix.modules.service.entity.Service;
import com.wafipix.wafipix.modules.service.entity.ServiceFeature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.features WHERE s.id = :id")
    Optional<Service> findByIdWithCategoryAndFeatures(@Param("id") UUID id);
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.faqs WHERE s.id = :id")
    Optional<Service> findByIdWithCategoryAndFaqs(@Param("id") UUID id);
    
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
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category WHERE s.active = true ORDER BY s.createdAt ASC")
    List<Service> findActiveServices();
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category WHERE s.active = true AND s.category.id = :categoryId ORDER BY s.createdAt ASC")
    List<Service> findActiveServicesByCategory(@Param("categoryId") UUID categoryId);
    
    @Query("SELECT s FROM Service s LEFT JOIN FETCH s.category LEFT JOIN FETCH s.packages WHERE s.slug = :slug AND s.active = true")
    Optional<Service> findBySlugAndActiveTrue(@Param("slug") String slug);

    @Modifying
    @Query("DELETE FROM ServiceFeature sf WHERE sf.service.id = :serviceId")
    void deleteAllFeaturesByServiceId(@Param("serviceId") UUID serviceId);

    @Modifying
    @Query("DELETE FROM FAQ f WHERE f.service.id = :serviceId")
    void deleteAllFaqsByServiceId(@Param("serviceId") UUID serviceId);

    @Modifying
    @Query("DELETE FROM Feature f WHERE f.packageEntity.id = :packageId")
    void deleteAllFeaturesByPackageId(@Param("packageId") UUID packageId);

    // Get service features ordered by creation time
    @Query("SELECT sf FROM ServiceFeature sf WHERE sf.service.id = :serviceId ORDER BY sf.createdAt ASC")
    List<ServiceFeature> findServiceFeaturesOrderedByCreation(@Param("serviceId") UUID serviceId);

    // Get service FAQs ordered by creation time
    @Query("SELECT f FROM FAQ f WHERE f.service.id = :serviceId ORDER BY f.createdAt ASC")
    List<FAQ> findServiceFaqsOrderedByCreation(@Param("serviceId") UUID serviceId);
}

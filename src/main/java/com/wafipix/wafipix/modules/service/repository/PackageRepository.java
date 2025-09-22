package com.wafipix.wafipix.modules.service.repository;

import com.wafipix.wafipix.modules.service.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<Package, UUID> {
    
    boolean existsByTitleIgnoreCaseAndServiceId(String title, UUID serviceId);
    
    boolean existsByTitleIgnoreCaseAndServiceIdAndIdNot(String title, UUID serviceId, UUID id);
    
    @Query("SELECT p FROM Package p LEFT JOIN FETCH p.service WHERE p.id = :id")
    java.util.Optional<Package> findByIdWithService(@Param("id") UUID id);
    
    @Query("SELECT p FROM Package p LEFT JOIN FETCH p.service WHERE p.service.id = :serviceId")
    List<Package> findByServiceId(@Param("serviceId") UUID serviceId);
    
    @Query("SELECT p FROM Package p LEFT JOIN FETCH p.service WHERE p.service.id = :serviceId AND p.status = :status")
    List<Package> findByServiceIdAndStatus(@Param("serviceId") UUID serviceId, @Param("status") com.wafipix.wafipix.modules.service.enums.PackageStatus status);
    
    // Public APIs - only active packages
    @Query("SELECT p FROM Package p LEFT JOIN FETCH p.service WHERE p.service.id = :serviceId AND p.status IN ('ACTIVE', 'FEATURED')")
    List<Package> findActivePackagesByServiceId(@Param("serviceId") UUID serviceId);
}

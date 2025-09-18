package com.wafipix.wafipix.modules.service.repository;

import com.wafipix.wafipix.modules.service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    boolean existsByTitleIgnoreCase(String title);
    
    boolean existsByTitleIgnoreCaseAndIdNot(String title, UUID id);
}

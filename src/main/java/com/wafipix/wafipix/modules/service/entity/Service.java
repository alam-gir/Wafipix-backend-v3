package com.wafipix.wafipix.modules.service.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "services")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service extends Auditable {
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(nullable = false, unique = true, length = 100)
    private String slug;
    
    @Column(length = 255)
    private String subtitle;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(length = 500)
    private String icon; // Public URL from file management
    
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Package> packages = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "service_features", joinColumns = @JoinColumn(name = "service_id"))
    private List<Feature> features;
    
    @ElementCollection
    @CollectionTable(name = "service_faqs", joinColumns = @JoinColumn(name = "service_id"))
    private List<FAQ> faqs;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}

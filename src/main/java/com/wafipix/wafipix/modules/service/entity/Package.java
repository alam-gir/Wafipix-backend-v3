package com.wafipix.wafipix.modules.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wafipix.wafipix.common.entity.Auditable;
import com.wafipix.wafipix.modules.service.enums.PackageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "packages")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Package extends Auditable {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(length = 255)
    private String subtitle;
    
    @Embedded
    private Pricing pricing;
    
    @OneToMany(mappedBy = "packageEntity", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Feature> features;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PackageStatus status;
    
    @Column(name = "delivery_time", length = 100)
    private String deliveryTime;
    
    @Column(name = "advance_percentage")
    private Double advancePercentage;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean popular = false;
}

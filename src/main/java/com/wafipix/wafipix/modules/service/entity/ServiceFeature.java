package com.wafipix.wafipix.modules.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "service_features")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFeature extends Auditable {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnore
    private Service service;
    
    @Column(nullable = false, length = 500)
    private String text;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean highlight = false;
}

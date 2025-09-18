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
@Table(name = "categories")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends Auditable {
    
    @Column(nullable = false, length = 100)
    private String title;
    
    @Column(length = 255)
    private String subtitle;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Service> services = new ArrayList<>();
}

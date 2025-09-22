package com.wafipix.wafipix.modules.work.entity;

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
@Table(name = "galleries")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gallery extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isMobileGrid = false;

    @OneToMany(mappedBy = "gallery", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<GalleryItem> items = new ArrayList<>();
}

package com.wafipix.wafipix.modules.work.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import com.wafipix.wafipix.modules.filemanagement.entity.File;
import com.wafipix.wafipix.modules.service.entity.Service;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "works")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Work extends Auditable {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_video_id")
    private File coverVideo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cover_image_id")
    private File coverImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_video_id")
    private File profileVideo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_image_id")
    private File profileImage;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Gallery> galleries = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}

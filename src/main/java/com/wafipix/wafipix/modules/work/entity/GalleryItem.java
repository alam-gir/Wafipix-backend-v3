package com.wafipix.wafipix.modules.work.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import com.wafipix.wafipix.modules.filemanagement.entity.File;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "gallery_items")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GalleryItem extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gallery_id", nullable = false)
    private Gallery gallery;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;
}

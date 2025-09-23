package com.wafipix.wafipix.modules.advertisementvideo.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "advertisement_videos")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertisementVideo extends Auditable {

    @Column(nullable = false, length = 500)
    private String url; // Public URL from file management
}

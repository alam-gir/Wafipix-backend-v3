package com.wafipix.wafipix.modules.socialmedia.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "social_media")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMedia extends Auditable {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String url;
}

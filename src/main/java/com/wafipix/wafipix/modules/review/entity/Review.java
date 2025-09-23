package com.wafipix.wafipix.modules.review.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "reviews")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends Auditable {

    @Column(nullable = false, length = 500)
    private String reviewImage; // Public URL from file management

    @Column(nullable = false, length = 100)
    private String platform; // Facebook, Fiverr, Google, etc.

    @Column(length = 100)
    private String clientName;

    @Column
    private Integer rating; // 1-5 stars

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}

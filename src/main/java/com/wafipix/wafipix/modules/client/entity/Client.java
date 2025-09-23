package com.wafipix.wafipix.modules.client.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clients")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client extends Auditable {

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String logo; // Public URL from file management

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String companyUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}

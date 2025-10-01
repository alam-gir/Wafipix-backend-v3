package com.wafipix.wafipix.modules.service.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "service_faqs")
@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FAQ extends Auditable {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnore
    private Service service;
    
    @Column(nullable = false, length = 1000)
    private String question;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;
}

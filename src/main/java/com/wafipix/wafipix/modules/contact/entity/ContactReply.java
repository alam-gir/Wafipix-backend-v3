package com.wafipix.wafipix.modules.contact.entity;

import com.wafipix.wafipix.common.entity.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "contact_replies")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactReply extends Auditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, length = 100)
    private String repliedBy; // Username who replied
}

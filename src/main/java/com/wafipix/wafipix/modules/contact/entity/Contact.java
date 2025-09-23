package com.wafipix.wafipix.modules.contact.entity;

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
@Table(name = "contacts")
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends Auditable {

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(length = 50)
    private String status; // "new", "read", "replied"

    @Column(length = 100)
    private String readBy; // Username who first read this contact

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ContactReply> replies = new ArrayList<>();
}

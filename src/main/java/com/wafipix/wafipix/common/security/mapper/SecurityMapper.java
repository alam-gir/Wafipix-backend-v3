package com.wafipix.wafipix.common.security.mapper;

import com.wafipix.wafipix.common.security.dto.AdminProfileResponse;
import com.wafipix.wafipix.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityMapper {
    public AdminProfileResponse toProfileResponse(User user) {
        if (user == null) {
            return null;
        }
        return AdminProfileResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
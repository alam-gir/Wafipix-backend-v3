package com.wafipix.wafipix.modules.service.dto.admin.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceSearchRequest {
    
    private String title;
    private String slug;
    private UUID categoryId;
    private Boolean active;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
    
    // Default values
    public Integer getPage() {
        return page != null ? page : 0;
    }
    
    public Integer getSize() {
        return size != null ? size : 10;
    }
    
    public String getSortBy() {
        return sortBy != null ? sortBy : "createdAt";
    }
    
    public String getSortDirection() {
        return sortDirection != null ? sortDirection : "desc";
    }
}

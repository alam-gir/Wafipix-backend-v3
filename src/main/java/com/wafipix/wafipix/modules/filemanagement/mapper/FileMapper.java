package com.wafipix.wafipix.modules.filemanagement.mapper;

import com.wafipix.wafipix.modules.filemanagement.dto.response.FileResponse;
import com.wafipix.wafipix.modules.filemanagement.entity.File;
import org.springframework.stereotype.Component;

@Component
public class FileMapper {

    public FileResponse toResponse(File file) {
        if (file == null) return null;

        return new FileResponse(
                file.getId(),
                file.getFileName(),
                file.getOriginalFileName(),
                file.getFilePath(),
                file.getPublicUrl(),
                file.getMimeType(),
                file.getFileSize(),
                file.getFileExtension(),
                file.getFolderPath(),
                file.getIsActive(),
                file.getDescription(),
                file.getCreatedAt(),
                file.getUpdatedAt()
        );
    }
}

package com.wafipix.wafipix.modules.filemanagement.repository;

import com.wafipix.wafipix.modules.filemanagement.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for File entity operations
 */
@Repository
public interface FileRepository extends JpaRepository<File, UUID> {

    /**
     * Find file by public URL
     */
    Optional<File> findByPublicUrl(String publicUrl);

    /**
     * Find file by file path in R2 bucket
     */
    Optional<File> findByFilePath(String filePath);

    /**
     * Find files by folder path
     */
    List<File> findByFolderPath(String folderPath);

    /**
     * Find files by file type
     */
    List<File> findByMimeTypeStartingWith(String mimeTypePrefix);

    /**
     * Find active files by folder path
     */
    List<File> findByFolderPathAndIsActiveTrue(String folderPath);

    /**
     * Find files by original file name
     */
    List<File> findByOriginalFileName(String originalFileName);

    /**
     * Check if file exists by public URL
     */
    boolean existsByPublicUrl(String publicUrl);

    /**
     * Check if file exists by file path
     */
    boolean existsByFilePath(String filePath);

    /**
     * Find files by multiple public URLs
     */
    @Query("SELECT f FROM File f WHERE f.publicUrl IN :urls")
    List<File> findByPublicUrlIn(@Param("urls") List<String> urls);

    /**
     * Find files by file extension
     */
    List<File> findByFileExtension(String fileExtension);

    /**
     * Find files by file size range
     */
    @Query("SELECT f FROM File f WHERE f.fileSize BETWEEN :minSize AND :maxSize")
    List<File> findByFileSizeBetween(@Param("minSize") Long minSize, @Param("maxSize") Long maxSize);
}

package com.wafipix.wafipix.modules.filemanagement.config;

import com.wafipix.wafipix.modules.filemanagement.service.FileStorageService;
import com.wafipix.wafipix.modules.filemanagement.service.impl.LocalFileStorageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuration factory for file storage services
 * Creates the appropriate storage service based on configuration
 */
@Configuration
@EnableConfigurationProperties(FileStorageConfig.class)
@Slf4j
public class FileStorageServiceFactory {
    
    @Autowired
    private FileStorageConfig config;
    
    /**
     * Primary file storage service bean
     * Automatically selects the correct implementation based on configuration
     */
    @Bean
    @Primary
    public FileStorageService fileStorageService() {
        String storageType = config.getStorage().getType().toLowerCase();
        
        log.info("Initializing file storage service with type: {}", storageType);
        log.info("File storage config: {}", config);
        
        switch (storageType) {
            case "local":
                log.info("Creating LocalFileStorageServiceImpl");
                return new LocalFileStorageServiceImpl(config);
            case "cloudflare":
                // For now, return local storage as fallback
                // TODO: Update CloudflareR2ServiceImpl to implement FileStorageService
                log.warn("Cloudflare storage not yet implemented with new interface, falling back to local storage");
                return new LocalFileStorageServiceImpl(config);
            default:
                throw new IllegalArgumentException("Unsupported storage type: " + storageType + 
                    ". Supported types: 'local', 'cloudflare'");
        }
    }
    
    /**
     * S3Client bean for Cloudflare R2
     * Only created when using Cloudflare storage
     */
    @Bean
    @ConditionalOnProperty(name = "file.storage.type", havingValue = "cloudflare")
    public S3Client s3Client() {
        try {
            FileStorageConfig.Cloudflare.R2 r2Config = config.getStorage().getCloudflare().getR2();
            
            AwsBasicCredentials credentials = AwsBasicCredentials.create(
                r2Config.getAccessKey(), 
                r2Config.getSecretKey()
            );
            
            S3Client client = S3Client.builder()
                    .endpointOverride(URI.create(r2Config.getEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .region(Region.of(r2Config.getRegion()))
                    .build();
            
            log.info("Cloudflare R2 S3 client configured successfully");
            return client;
            
        } catch (Exception e) {
            log.error("Failed to configure Cloudflare R2 S3 client: {}", e.getMessage());
            throw new RuntimeException("Failed to configure Cloudflare R2 S3 client", e);
        }
    }
}

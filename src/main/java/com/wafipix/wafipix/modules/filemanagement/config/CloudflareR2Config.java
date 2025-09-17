package com.wafipix.wafipix.modules.filemanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

/**
 * Configuration for Cloudflare R2 S3 client
 * Sets up the S3 client to work with Cloudflare R2 storage
 */
@Configuration
@Slf4j
public class CloudflareR2Config {

    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;

    @Value("${cloudflare.r2.access-key}")
    private String accessKey;

    @Value("${cloudflare.r2.secret-key}")
    private String secretKey;

    @Value("${cloudflare.r2.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
            
            S3Client client = S3Client.builder()
                    .endpointOverride(URI.create(endpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(credentials))
                    .region(Region.of(region))
                    .build();
            
            log.info("Cloudflare R2 S3 client configured successfully");
            return client;
            
        } catch (Exception e) {
            log.error("Failed to configure Cloudflare R2 S3 client: {}", e.getMessage());
            throw new RuntimeException("Failed to configure Cloudflare R2 S3 client", e);
        }
    }
}

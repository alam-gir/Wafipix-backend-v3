package com.wafipix.wafipix.modules.filemanagement.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for file storage
 * Supports both local file system and Cloudflare R2 storage
 */
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageConfig {
    
    private Storage storage = new Storage();
    
    @Data
    public static class Storage {
        private String type = "local"; // default to local storage
        private Local local = new Local();
        private Cloudflare cloudflare = new Cloudflare();
    }
    
    @Data
    public static class Local {
        private String basePath = "./uploads";
        private String publicUrl = "http://localhost:8080/v3/public/uploads";
        private String maxFileSize = "200MB";
    }
    
    @Data
    public static class Cloudflare {
        private R2 r2 = new R2();
        
        @Data
        public static class R2 {
            private String endpoint;
            private String accessKey;
            private String secretKey;
            private String region = "auto";
            private String bucketName;
            private String publicUrl;
        }
    }
}

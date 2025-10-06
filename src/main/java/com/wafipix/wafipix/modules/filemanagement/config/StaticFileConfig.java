package com.wafipix.wafipix.modules.filemanagement.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Static file serving configuration
 * Serves uploaded files when using local storage
 */
@Configuration
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local")
@Slf4j
public class StaticFileConfig implements WebMvcConfigurer {
    
    @Autowired
    private FileStorageConfig config;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String basePath = config.getStorage().getLocal().getBasePath();
        String publicUrl = config.getStorage().getLocal().getPublicUrl();
        
        log.info("Configuring static file serving:");
        log.info("  Base Path: {}", basePath);
        log.info("  Public URL: {}", publicUrl);
        
        // Extract the URL path from public URL
        // e.g., "/v3/public/uploads" from "http://localhost:8080/v3/public/uploads"
        String urlPath;
        try {
            int protocolEnd = publicUrl.indexOf("://");
            if (protocolEnd != -1) {
                int pathStart = publicUrl.indexOf("/", protocolEnd + 3);
                urlPath = pathStart != -1 ? publicUrl.substring(pathStart) : "/";
            } else {
                urlPath = publicUrl.startsWith("/") ? publicUrl : "/" + publicUrl;
            }
        } catch (Exception e) {
            log.warn("Failed to parse public URL, using default: {}", e.getMessage());
            urlPath = "/v3/public/uploads";
        }
        
        // Ensure base path is absolute
        String absoluteBasePath = basePath.startsWith("./") ? 
            System.getProperty("user.dir") + "/" + basePath.substring(2) : basePath;
        
        log.info("  Extracted URL Path: {}", urlPath);
        log.info("  Absolute Base Path: {}", absoluteBasePath);
        
        // Add resource handler with cache settings for better performance
        registry.addResourceHandler(urlPath + "/**")
                .addResourceLocations("file:" + absoluteBasePath + "/")
                .setCachePeriod(3600) // Cache for 1 hour
                .resourceChain(true); // Enable resource chain for better performance
        
        log.info("Static file serving configured for local storage:");
        log.info("  URL Path: {}", urlPath + "/**");
        log.info("  File Location: {}", "file:" + absoluteBasePath + "/");
        log.info("  Public URL: {}", publicUrl);
        log.info("  Cache Period: 3600 seconds");
    }
}


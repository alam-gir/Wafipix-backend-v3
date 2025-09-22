package com.wafipix.wafipix.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Utility class for generating SEO-friendly slugs from text
 * Can be reused across the application for any slug generation needs
 */
public class SlugUtil {
    
    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");
    
    /**
     * Generate a SEO-friendly slug from the given text
     * 
     * @param input The text to convert to slug
     * @return SEO-friendly slug
     */
    public static String generateSlug(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        
        // Normalize unicode characters
        String normalized = Normalizer.normalize(input.trim(), Normalizer.Form.NFD);
        
        // Convert to lowercase
        String lowercased = normalized.toLowerCase(Locale.ENGLISH);
        
        // Replace whitespace with dashes
        String noWhitespace = WHITESPACE.matcher(lowercased).replaceAll("-");
        
        // Remove non-latin characters
        String slug = NON_LATIN.matcher(noWhitespace).replaceAll("");
        
        // Remove multiple consecutive dashes
        slug = slug.replaceAll("-+", "-");
        
        // Remove leading and trailing dashes
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        
        // Ensure slug is not empty
        if (slug.isEmpty()) {
            slug = "untitled";
        }
        
        // Limit length to 100 characters
        if (slug.length() > 100) {
            slug = slug.substring(0, 100);
            // Remove trailing dash if it exists
            if (slug.endsWith("-")) {
                slug = slug.substring(0, slug.length() - 1);
            }
        }
        
        return slug;
    }
    
    /**
     * Generate a unique slug by appending a number if the base slug already exists
     * 
     * @param baseSlug The base slug
     * @param slugChecker Function to check if slug exists
     * @return Unique slug
     */
    public static String generateUniqueSlug(String baseSlug, SlugChecker slugChecker) {
        String slug = baseSlug;
        int counter = 1;
        
        while (slugChecker.exists(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
    
    /**
     * Functional interface for checking if a slug exists
     */
    @FunctionalInterface
    public interface SlugChecker {
        boolean exists(String slug);
    }
}

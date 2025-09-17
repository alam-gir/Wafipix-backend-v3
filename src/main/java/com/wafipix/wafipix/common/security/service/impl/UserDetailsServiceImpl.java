package com.wafipix.wafipix.common.security.service.impl;

import com.wafipix.wafipix.common.exception.BusinessException;
import com.wafipix.wafipix.modules.user.entity.User;
import com.wafipix.wafipix.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.debug("Loading user details for username: {}", username);
            
            User user = userRepository.findByEmailIgnoreCase(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
            
            log.debug("Successfully loaded user details for: {}", username);
            return new UserDetailsImpl(user);
            
        } catch (UsernameNotFoundException e) {
            log.warn("User not found: {}", username);
            throw e;
        } catch (BusinessException e) {
            log.error("Business error loading user details for {}: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error loading user details for {}: {}", username, e.getMessage(), e);
            throw new BusinessException("Failed to load user details: " + e.getMessage());
        }
    }
}

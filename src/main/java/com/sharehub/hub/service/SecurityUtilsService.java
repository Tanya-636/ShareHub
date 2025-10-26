package com.sharehub.hub.service;

import com.sharehub.hub.entity.User;
import com.sharehub.hub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUtilsService {

    private final UserRepository userRepository; // Dependency is injected here

    /**
     * Retrieves the Long ID of the currently authenticated user by resolving the email
     * from the Spring Security context.
     *
     * @return The authenticated user's database ID (Long).
     */
    public Long getAuthenticatedUserId() {
        // 1. Get the authenticated principal's name (the email from your JWT)
        String principalEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (principalEmail == null || principalEmail.isEmpty() || principalEmail.equals("anonymousUser")) {
            throw new IllegalStateException("User is not authenticated. Authentication context is empty.");
        }

        // 2. Look up the full User entity using the email
        User user = userRepository.findByEmail(principalEmail)
                .orElseThrow(() -> new EntityNotFoundException("Authenticated user email not found in database: " + principalEmail));

        // 3. Return the database ID
        return user.getId();
    }
}
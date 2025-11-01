package com.sharehub.hub.controller;

import com.sharehub.hub.entity.User;
import com.sharehub.hub.service.UserService;
import com.sharehub.hub.service.SecurityUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtilsService securityUtilsService;

    /**
     * Gets the profile details of the authenticated user.
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<User> getAuthenticatedUserProfile() {
        Long userId = securityUtilsService.getAuthenticatedUserId();
        // UserService.getUserById is marked as readOnly=true, fetching safe data
        User user = userService.getUserById(userId);

        // IMPORTANT: You should create a UserProfileResponse DTO here
        // to strip sensitive data (like the password hash) before returning!

        return ResponseEntity.ok(user);
    }

    // NOTE: For a real project, you would also implement PUT /api/users/profile

}
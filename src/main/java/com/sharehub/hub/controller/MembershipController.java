package com.sharehub.hub.controller;

import com.sharehub.hub.dto.MemberAdditionRequest;
import com.sharehub.hub.entity.User;
import com.sharehub.hub.service.MembershipService;
import com.sharehub.hub.service.SecurityUtilsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/members") // Base path for member actions
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;
    private final SecurityUtilsService securityUtilsService; // Used to get Admin/User ID

    /**
     * ADMIN ACTION: Adds a new member to the group roster.
     * POST /api/groups/{groupId}/members
     */
    @PostMapping
    public ResponseEntity<Void> addMember(
            @PathVariable Long groupId,
            @Valid @RequestBody MemberAdditionRequest request) {

        Long adminId = securityUtilsService.getAuthenticatedUserId();

        // Service handles authorization (403 check) and member creation
        membershipService.addMember(adminId, groupId, request.getMemberEmail());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    /**
     * Lists all active members in a group.
     * GET /api/groups/{groupId}/members
     */
    @GetMapping
    public ResponseEntity<List<User>> getGroupMembers(@PathVariable Long groupId) {
        Long userId = securityUtilsService.getAuthenticatedUserId();

        // Service handles authorization (must be an active member to view list)
        List<User> members = membershipService.getActiveMembers(userId, groupId);

        return ResponseEntity.ok(members); // 200 OK
    }
}
package com.sharehub.hub.controller;

import com.sharehub.hub.dto.GroupCreationRequest;
import com.sharehub.hub.dto.GroupResponse; // <-- Use DTO for output
import com.sharehub.hub.entity.Group;
import com.sharehub.hub.service.GroupService;
import com.sharehub.hub.service.SecurityUtilsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;
    private final SecurityUtilsService securityUtilsService; // Injected for User ID retrieval

    /**
     * Creates a new group and automatically makes the creator the Admin.
     * POST /api/groups
     */
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupCreationRequest request) {
        Long creatorId = securityUtilsService.getAuthenticatedUserId();

        // 1. Service handles creation and initial Membership
        Group newGroup = groupService.createGroup(creatorId, request.getGroupName());

        // 2. Convert entity to the safe DTO for the response
        GroupResponse response = new GroupResponse(newGroup);

        return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
    }

    /**
     * Retrieves all groups where the authenticated user is an active member.
     * GET /api/groups
     */
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups() {
        Long userId = securityUtilsService.getAuthenticatedUserId();

        // 1. Service retrieves List<Group> entities
        List<Group> groups = groupService.getUserGroups(userId);

        // 2. Convert List<Group> entities to List<GroupResponse> DTOs
        List<GroupResponse> responses = groups.stream()
                .map(GroupResponse::new) // Uses the DTO constructor for mapping
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses); // 200 OK
    }
}
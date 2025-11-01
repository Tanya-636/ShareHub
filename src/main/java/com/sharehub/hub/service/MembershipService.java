package com.sharehub.hub.service;

import com.sharehub.hub.entity.*;
import com.sharehub.hub.exception.AccessDeniedException;
import com.sharehub.hub.repository.GroupRepository;
import com.sharehub.hub.repository.MembershipRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final UserService userService;
    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;

    // --- Authorization Checks (Guaranteed Read Consistency) ---

    @Transactional
    public void checkAdminPermission(Long adminId, Long groupId) {
        // Uses the highly specific query to check role, status, user, and group atomically
        boolean isAdminActive = membershipRepository.findSpecificMembership(
                groupId,
                adminId,
                GroupRole.ADMIN,
                MembershipStatus.ACTIVE
        ).isPresent();

        if (!isAdminActive) {
            throw new AccessDeniedException("User is not an active admin of this group.");
        }
    }

    @Transactional
    public void checkActiveMembership(Long userId, Long groupId) {
        // Checks if ANY active membership (Admin or Member) exists
        boolean isActive = membershipRepository.findAnyActiveMembership(
                groupId,
                userId,
                MembershipStatus.ACTIVE
        ).isPresent();

        if (!isActive) {
            // This covers failure if the user is not a member OR is a member but status is REMOVED
            throw new AccessDeniedException("User is not an active member of this group.");
        }
    }

    // --- Roster Management (Read-Write) ---

    @Transactional
    public Membership addMember(Long adminId, Long groupId, String memberEmail) {
        // 1. Authorization Check
        checkAdminPermission(adminId, groupId);

        // 2. Retrieve Entities
        User memberToAdd = userService.getUserByEmail(memberEmail);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found."));

        // 3. Prevent Duplicates/Update Status if REMOVED
        Optional<Membership> existing = membershipRepository.findByGroupAndUser(group, memberToAdd);

        if (existing.isPresent()) {
            Membership m = existing.get();
            if (m.getStatus() == MembershipStatus.ACTIVE) {
                // Already active, nothing to do
                return m;
            } else if (m.getStatus() == MembershipStatus.REMOVED) {
                // Update status from REMOVED to ACTIVE (Re-add)
                m.setStatus(MembershipStatus.ACTIVE);
                return membershipRepository.save(m);
            }
        }

        // 4. Create New Membership (Direct Add)
        Membership newMembership = Membership.builder()
                .user(memberToAdd)
                .group(group)
                .role(GroupRole.MEMBER) // Default to MEMBER role
                .status(MembershipStatus.ACTIVE)
                .build();

        return membershipRepository.save(newMembership);
    }

    @Transactional(readOnly = true)
    public List<User> getActiveMembers(Long userId, Long groupId) {
        // Authorization: Any active member can see the list of members
        checkActiveMembership(userId, groupId);

        // Uses the custom repository method to fetch users directly
        List<User> members = membershipRepository.findAllActiveUsersByGroupId(
                groupId,
                MembershipStatus.ACTIVE
        );

        // FINAL SAFETY NET: Filters out any null User objects that resulted from orphaned foreign keys
        // (This prevents the API from crashing or returning null elements)
        return members.stream()
                .filter(user -> user != null)
                .collect(Collectors.toList());
    }
}
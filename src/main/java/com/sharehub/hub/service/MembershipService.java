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

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final UserService userService;
    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;

    // --- Authorization Checks (Transactional for consistency fix) ---

    // In MembershipService.java

    // In MembershipService.java

    @Transactional
    public void checkAdminPermission(Long adminId, Long groupId) {
        // We no longer need to fetch the User/Group objects just for the check (improving efficiency)

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

    @Transactional // FIX: Standard transaction ensures visibility of recent commits
    public void checkActiveMembership(Long userId, Long groupId) {
        User user = userService.getUserById(userId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found."));

        // Check if user is an ACTIVE member (any role)
        boolean isActive = membershipRepository
                .findByGroupAndUser(group, user)
                .filter(m -> m.getStatus() == MembershipStatus.ACTIVE)
                .isPresent();

        if (!isActive) {
            throw new AccessDeniedException("User is not an active member of this group.");
        }
    }

    // --- Roster Management (Read-Write) ---

    @Transactional
    public Membership addMember(Long adminId, Long groupId, String memberEmail) {
        // 1. Authorization Check (Uses the fixed method above)
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
        checkActiveMembership(userId, groupId); // Uses the fixed method

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found."));

        return membershipRepository.findAllByGroupAndStatus(group, MembershipStatus.ACTIVE)
                .stream()
                .map(Membership::getUser)
                .toList();
    }
}
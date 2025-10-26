package com.sharehub.hub.service;

import com.sharehub.hub.entity.*;
import com.sharehub.hub.repository.GroupRepository;
import com.sharehub.hub.repository.MembershipRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final UserService userService;
    private final GroupRepository groupRepository;
    private final MembershipRepository membershipRepository;
    private final EntityManager entityManager;

    // In com.sharehub.hub.service.GroupService.java

    @Transactional
    public Group createGroup(Long creatorId, String groupName) {
        User creator = userService.getUserById(creatorId);

        // 1. Create and Save the Group
        Group group = Group.builder()
                .name(groupName)
                .createdBy(creator)
                .build();
        Group savedGroup = groupRepository.save(group);

        // --- AGGRESSIVE PERSISTENCE ---
        entityManager.flush();
        entityManager.clear();

        // 2. Re-fetch the Group Entity
        Group freshGroup = groupRepository.findById(savedGroup.getId())
                .orElseThrow(() -> new EntityNotFoundException("Group creation failed unexpectedly."));

        // 3. Create the initial ADMIN Membership
        Membership adminMembership = Membership.builder()
                .user(creator)
                .group(freshGroup)
                .role(GroupRole.ADMIN)
                .status(MembershipStatus.ACTIVE)
                .build();

        // Hibernate is executing the INSERT for Membership here.
        membershipRepository.save(adminMembership);

        return freshGroup;
    }

    @Transactional(readOnly = true)
    public List<Group> getUserGroups(Long userId) {
        User user = userService.getUserById(userId);

        return membershipRepository.findAllByUserAndStatus(user, MembershipStatus.ACTIVE)
                .stream()
                .map(Membership::getGroup)
                .toList();
    }
}
package com.sharehub.hub.repository;

import com.sharehub.hub.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    // --- Basic Lookups ---
    Optional<Membership> findByGroupAndUser(Group group, User user);
    Optional<Membership> findByGroupAndUserId(Group group, Long userId);
    Optional<Membership> findByGroupAndUserAndStatus(Group group, User user, MembershipStatus status);

    // --- Active Membership List Lookups ---
    List<Membership> findAllByGroupAndStatus(Group group, MembershipStatus status);
    List<Membership> findAllByUserAndStatus(User user, MembershipStatus status);

    // --- Complex Authorization Check (Used by checkAdminPermission) ---
    @Query("SELECT m FROM Membership m WHERE m.group.id = :groupId AND m.user.id = :userId AND m.role = :role AND m.status = :status")
    Optional<Membership> findSpecificMembership(
            @Param("groupId") Long groupId,
            @Param("userId") Long userId,
            @Param("role") GroupRole role,
            @Param("status") MembershipStatus status
    );

    // --- Roster Listing Query (CRITICAL ADDITION for getActiveMembers) ---
    // Selects the User object (m.user) directly from the Membership relationship
    @Query("SELECT m.user FROM Membership m WHERE m.group.id = :groupId AND m.status = :status")
    List<User> findAllActiveUsersByGroupId(
            @Param("groupId") Long groupId,
            @Param("status") MembershipStatus status
    );
    @Query("SELECT m FROM Membership m WHERE m.group.id = :groupId AND m.user.id = :userId AND m.status = :status")
    Optional<Membership> findAnyActiveMembership(
            @Param("groupId") Long groupId,
            @Param("userId") Long userId,
            @Param("status") MembershipStatus status
    );
}
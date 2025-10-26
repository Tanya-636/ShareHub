package com.sharehub.hub.repository;

import com.sharehub.hub.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership, Long> {


    Optional<Membership> findByGroupAndUser(Group group, User user);
    Optional<Membership> findByGroupAndUserId(Group group, Long userId);

    Optional<Membership> findByGroupAndUserAndStatus(Group group, User user, MembershipStatus status);

    List<Membership> findAllByGroupAndStatus(Group group, MembershipStatus status);


    List<Membership> findAllByUserAndStatus(User user, MembershipStatus status);
    // In MembershipRepository.java

    @Query("SELECT m FROM Membership m WHERE m.group.id = :groupId AND m.user.id = :userId AND m.role = :role AND m.status = :status")
    Optional<Membership> findSpecificMembership(
            @Param("groupId") Long groupId,
            @Param("userId") Long userId,
            @Param("role") GroupRole role,
            @Param("status") MembershipStatus status
    );
}
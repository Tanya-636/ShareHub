package com.sharehub.hub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Ensure a user has only one membership per group
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "group_id"})
})
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User in the group - Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Group to which the user belongs - Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    // Role in this group (ADMIN, MEMBER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupRole role;

    // Status of the membership (ACTIVE, REMOVED)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MembershipStatus status;
}
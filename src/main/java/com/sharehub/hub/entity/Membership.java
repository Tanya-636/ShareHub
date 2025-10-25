package com.sharehub.hub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User in the group
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Group to which the user belongs
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    // Role in this group (e.g., "ADMIN" or "MEMBER")
    @Column(nullable = false)
    private String role;
}

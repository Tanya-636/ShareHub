package com.sharehub.hub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "createdBy")
    private Set<Group> createdGroups;

    @OneToMany(mappedBy = "user")
    private Set<Membership> memberships;

    @OneToMany(mappedBy = "uploadedBy")
    private Set<File> uploadedFiles;
}
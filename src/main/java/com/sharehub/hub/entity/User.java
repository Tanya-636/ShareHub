package com.sharehub.hub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore; // <-- NEW IMPORT NEEDED

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

    // --- BREAKS THE SERIALIZATION LOOP ---

    // 1. Breaks the User -> Group -> User cycle.
    @OneToMany(mappedBy = "createdBy")
    @JsonIgnore
    private Set<Group> createdGroups;

    // 2. Breaks the User -> Membership -> Group -> User cycle.
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private Set<Membership> memberships;

    // 3. Recommended: Ignore File collection too, as it links to Group.
    @OneToMany(mappedBy = "uploadedBy")
    @JsonIgnore
    private Set<File> uploadedFiles;
}
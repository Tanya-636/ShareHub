package com.sharehub.hub.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // Creator of the group
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    // Members of this group
    @OneToMany(mappedBy = "group")
    private Set<Membership> memberships;

    // Files uploaded to this group
    @OneToMany(mappedBy = "group")
    private Set<File> files;
}

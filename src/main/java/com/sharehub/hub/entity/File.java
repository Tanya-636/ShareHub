package com.sharehub.hub.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private String filePath; // path in server or cloud storage

    // The group this file belongs to
    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    // The user who uploaded the file
    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}


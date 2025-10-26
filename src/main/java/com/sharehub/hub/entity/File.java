package com.sharehub.hub.entity;

import com.sharehub.hub.entity.Group;
import com.sharehub.hub.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ... (Metadata fields remain the same)
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false)
    private String storagePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();

    // The group this file belongs to. Now nullable (can be null for private files).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true) // <-- CRUCIAL CHANGE: nullable = true
    private Group group;

    // The user who uploaded the file - Required
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    // NEW FIELD: Indicates if the file is private (true) or belongs to a group (false)
    @Column(nullable = false)
    private boolean isPrivate = true; // Default to true if group is null, or set explicitly
}
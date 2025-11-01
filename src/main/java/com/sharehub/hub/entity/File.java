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

    // ... (Metadata fields) ...
    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private Long sizeBytes;

    @Column(nullable = false)
    private String storagePath;

    // 1. Removed the unreliable inline default (= LocalDateTime.now())
    // 2. Set 'updatable = false' as it should only be set on creation
    @Column(nullable = false, updatable = false)
    private LocalDateTime uploadDate; // No initial value here

    // ... (Relationships) ...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by_user_id", nullable = false)
    private User uploadedBy;

    @Column(nullable = false)
    private boolean isPrivate = true;

    /**
     * JPA Lifecycle Callback: Ensures the uploadDate is set immediately before
     * the database INSERT occurs, guaranteeing the value is non-null.
     */
    @PrePersist // <-- CRITICAL FIX: Executes right before persistence
    protected void onCreate() {
        // Set the timestamp only if it hasn't been explicitly set (e.g., in a unit test)
        if (uploadDate == null) {
            uploadDate = LocalDateTime.now();
        }
    }
}
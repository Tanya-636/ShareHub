package com.sharehub.hub.service;

import com.sharehub.hub.entity.File;
import com.sharehub.hub.entity.Group;
import com.sharehub.hub.entity.User;
import com.sharehub.hub.exception.AccessDeniedException;
import com.sharehub.hub.repository.FileRepository;
import com.sharehub.hub.repository.GroupRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final UserService userService;
    private final GroupRepository groupRepository;
    private final FileRepository fileRepository;
    private final MembershipService membershipService;
    private final CloudinaryStorageService storageService;

    // --- Upload Logic ---

    @Transactional
    public File uploadPrivateFile(Long uploaderId, MultipartFile file) throws IOException {
        User uploader = userService.getUserById(uploaderId);
        // Use a unique folder structure tied to the user ID
        String folder = "private_user_" + uploaderId;

        // 1. Upload to Cloudinary
        String storagePath = storageService.uploadFile(file, folder);

        // 2. Save File Metadata
        File fileEntity = File.builder()
                .fileName(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .sizeBytes(file.getSize())
                .storagePath(storagePath)
                .uploadedBy(uploader)
                .isPrivate(true)
                .group(null)
                .build();

        return fileRepository.save(fileEntity);
    }

    @Transactional
    public File uploadSharedFile(Long uploaderId, Long groupId, MultipartFile file) throws IOException {
        // 1. Authorization Check: Must be an active member of the group
        membershipService.checkActiveMembership(uploaderId, groupId);

        User uploader = userService.getUserById(uploaderId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found."));
        String folder = "group_" + groupId;

        // 2. Upload to Cloudinary
        String storagePath = storageService.uploadFile(file, folder);

        // 3. Save File Metadata
        File fileEntity = File.builder()
                .fileName(file.getOriginalFilename())
                .mimeType(file.getContentType())
                .sizeBytes(file.getSize())
                .storagePath(storagePath)
                .uploadedBy(uploader)
                .isPrivate(false)
                .group(group)
                .build();

        return fileRepository.save(fileEntity);
    }

    // --- Access Logic ---

    @Transactional(readOnly = true)
    public List<File> getPrivateFiles(Long userId) {
        User user = userService.getUserById(userId);
        return fileRepository.findAllByUploadedByAndIsPrivateTrue(user);
    }

    @Transactional(readOnly = true)
    public List<File> getSharedFiles(Long userId, Long groupId) {
        // 1. Authorization Check: Must be an active member of the group
        membershipService.checkActiveMembership(userId, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found."));

        return fileRepository.findAllByGroupAndIsPrivateFalse(group);
    }

    @Transactional(readOnly = true)
    public String getFileDownloadUrl(Long userId, Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found."));

        // 1. Central Authorization Check
        if (file.isPrivate()) {
            // Private File Check: Must be the uploader
            if (!file.getUploadedBy().getId().equals(userId)) {
                throw new AccessDeniedException("You do not have permission to access this private file.");
            }
        } else {
            // Shared File Check: Must be an active member of the group
            Long groupId = file.getGroup().getId();
            membershipService.checkActiveMembership(userId, groupId);
        }

        // 2. Return the secure Cloudinary URL
        return file.getStoragePath();
    }
}
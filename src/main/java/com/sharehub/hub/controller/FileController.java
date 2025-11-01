package com.sharehub.hub.controller;

import com.sharehub.hub.entity.File;
import com.sharehub.hub.service.FileService;
import com.sharehub.hub.service.SecurityUtilsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType; // <-- NEW IMPORT NEEDED
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final SecurityUtilsService securityUtilsService;

    // --- Private Space Endpoints ---

    @PostMapping(value = "/private", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // <-- FIX APPLIED
    public ResponseEntity<File> uploadPrivateFile(@RequestParam("file") MultipartFile file) throws IOException {
        Long uploaderId = securityUtilsService.getAuthenticatedUserId();
        File savedFile = fileService.uploadPrivateFile(uploaderId, file);
        return new ResponseEntity<>(savedFile, HttpStatus.CREATED);
    }

    @GetMapping("/private") // List files in the user's private space
    public ResponseEntity<List<File>> getPrivateFiles() {
        Long userId = securityUtilsService.getAuthenticatedUserId();
        List<File> files = fileService.getPrivateFiles(userId);
        return ResponseEntity.ok(files);
    }

    // --- Shared Group Endpoints ---

    @PostMapping(value = "/groups/{groupId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // <-- FIX APPLIED
    public ResponseEntity<File> uploadSharedFile(
            @PathVariable Long groupId,
            @RequestParam("file") MultipartFile file) throws IOException {

        Long uploaderId = securityUtilsService.getAuthenticatedUserId();
        // Service handles membership check
        File savedFile = fileService.uploadSharedFile(uploaderId, groupId, file);
        return new ResponseEntity<>(savedFile, HttpStatus.CREATED);
    }

    @GetMapping("/groups/{groupId}") // List shared files in a group
    public ResponseEntity<List<File>> getSharedFiles(@PathVariable Long groupId) {
        Long userId = securityUtilsService.getAuthenticatedUserId();
        // Service handles membership check
        List<File> files = fileService.getSharedFiles(userId, groupId);
        return ResponseEntity.ok(files);
    }

    // --- Download/Access Endpoint ---

    @GetMapping("/{fileId}/url")
    public ResponseEntity<String> getFileDownloadUrl(@PathVariable Long fileId) {
        Long userId = securityUtilsService.getAuthenticatedUserId();
        // Service handles all authorization (private owner OR active group member)
        String downloadUrl = fileService.getFileDownloadUrl(userId, fileId);
        return ResponseEntity.ok(downloadUrl); // Returns the secure Cloudinary URL
    }
}
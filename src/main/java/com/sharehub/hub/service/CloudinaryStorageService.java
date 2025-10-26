package com.sharehub.hub.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryStorageService {

    private final Cloudinary cloudinary;

    // Spring injects the configured Cloudinary bean
    public CloudinaryStorageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads a file to Cloudinary and returns the secure URL for database storage.
     * * @param file The file to upload.
     * @param folder The folder used for organization (e.g., "group_123" or "private_user_456").
     * @return The secure URL (the storagePath) for the delivered file.
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            // resource_type: "auto" lets Cloudinary correctly handle image, video/audio, and raw files (DOCX, PDF)
            Map uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "sharehub/" + folder,
                            "resource_type", "auto"
                    )
            );
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            // You should implement better logging here
            throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Deletes a file from Cloudinary (using the public ID, which may need to be derived from the stored secure URL).
     */
    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
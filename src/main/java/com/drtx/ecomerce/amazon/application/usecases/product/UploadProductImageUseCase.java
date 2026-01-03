package com.drtx.ecomerce.amazon.application.usecases.product;

import com.drtx.ecomerce.amazon.core.ports.out.ImageStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

@Service
public class UploadProductImageUseCase {

    private final ImageStoragePort imageStoragePort;
    private final long maxFileSize;
    private final int maxFilesCount;
    private final long maxTotalSize;
    private final List<String> allowedContentTypes;

    public UploadProductImageUseCase(
            ImageStoragePort imageStoragePort,
            @Value("${storage.image.max-file-size}") long maxFileSize,
            @Value("${storage.image.max-files-count}") int maxFilesCount,
            @Value("${storage.image.max-total-size}") long maxTotalSize,
            @Value("${storage.image.allowed-content-types}") String allowedContentTypes) {
        this.imageStoragePort = imageStoragePort;
        this.maxFileSize = maxFileSize;
        this.maxFilesCount = maxFilesCount;
        this.maxTotalSize = maxTotalSize;
        this.allowedContentTypes = Arrays.asList(allowedContentTypes.split(","));
    }

    public List<String> uploadImages(Long userId, String userRole, List<MultipartFile> files) {
        // 1. Check Role
        if (!"SELLER".equals(userRole)) {
            throw new RuntimeException("Access Denied: Only SELLER can upload product images"); // Should be a custom
                                                                                                // Exception
        }

        // 2. Validate Count
        if (files.size() > maxFilesCount) {
            throw new RuntimeException("Too many files. Max allowed: " + maxFilesCount);
        }

        long totalSize = 0;
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            // 3. Validate Individual Size
            if (file.getSize() > maxFileSize) {
                throw new RuntimeException(
                        "File too large: " + file.getOriginalFilename() + ". Max allowed: " + maxFileSize);
            }
            totalSize += file.getSize();

            // 4. Validate Content Type
            if (!allowedContentTypes.contains(file.getContentType())) {
                throw new RuntimeException("Invalid file type: " + file.getContentType());
            }

            // 5. Check Total Size (accumulated)
            if (totalSize > maxTotalSize) {
                throw new RuntimeException("Total size limit exceeded. Max allowed: " + maxTotalSize);
            }
        }

        // 6. Upload
        for (MultipartFile file : files) {
            try {
                String extension = getExtension(file.getOriginalFilename());
                String fileName = UUID.randomUUID().toString() + "." + extension;
                String url = imageStoragePort.uploadImage(fileName, file.getInputStream(), file.getSize(),
                        file.getContentType());
                uploadedUrls.add(url);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload file", e);
            }
        }

        return uploadedUrls;
    }

    private String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}

package com.drtx.ecomerce.amazon.application.usecases.product;

import com.drtx.ecomerce.amazon.core.model.exceptions.DomainExceptionFactory;
import com.drtx.ecomerce.amazon.core.model.product.ImageFile;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UploadProductImageUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.out.ImageStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class UploadProductImageUseCase implements UploadProductImageUseCasePort {

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

    @Override
    public List<String> uploadImages(Long userId, String userRole, List<ImageFile> files) {
        // 1. Check Role - This should ideally be done at controller level with
        // @PreAuthorize
        // but keeping validation here for defense in depth
        if (!"SELLER".equals(userRole)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Only SELLER can upload product images");
        }

        // 2. Validate Count
        if (files.size() > maxFilesCount) {
            throw DomainExceptionFactory.tooManyImages(files.size(), maxFilesCount);
        }

        long totalSize = 0;
        List<String> uploadedUrls = new ArrayList<>();

        for (ImageFile file : files) {
            // 3. Validate Individual Size
            if (file.getSize() > maxFileSize) {
                throw DomainExceptionFactory.imageTooLarge(file.getSize(), maxFileSize);
            }
            totalSize += file.getSize();

            // 4. Validate Content Type
            if (!allowedContentTypes.contains(file.getContentType())) {
                throw DomainExceptionFactory.invalidImageFormat(file.getContentType());
            }

            // 5. Check Total Size (accumulated)
            if (totalSize > maxTotalSize) {
                throw DomainExceptionFactory.imageTooLarge(totalSize, maxTotalSize);
            }
        }

        // 6. Upload
        for (ImageFile file : files) {
            try {
                String extension = getExtension(file.getFileName());
                String fileName = UUID.randomUUID().toString() + "." + extension;
                String url = imageStoragePort.uploadImage(
                        fileName,
                        file.getContent(),
                        file.getSize(),
                        file.getContentType());
                uploadedUrls.add(url);
            } catch (Exception e) {
                throw DomainExceptionFactory.imageUploadFailed(file.getFileName(), e);
            }
        }

        return uploadedUrls;
    }

    private String getExtension(String filename) {
        if (filename == null)
            return "";
        int lastIndex = filename.lastIndexOf(".");
        return (lastIndex == -1) ? "" : filename.substring(lastIndex + 1);
    }
}

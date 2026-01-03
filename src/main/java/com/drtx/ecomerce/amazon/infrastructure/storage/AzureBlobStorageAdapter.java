package com.drtx.ecomerce.amazon.infrastructure.storage;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.drtx.ecomerce.amazon.core.ports.out.ImageStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AzureBlobStorageAdapter implements ImageStoragePort {

    private final BlobContainerClient containerClient;

    public AzureBlobStorageAdapter(
            @Value("${storage.azure.connection-string}") String connectionString,
            @Value("${storage.azure.container-name}") String containerName) {

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        this.containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
        }
    }

    @Override
    public String uploadImage(String fileName, InputStream content, long length, String contentType) {
        try {
            String path = "products/" + fileName;
            BlobClient blobClient = containerClient.getBlobClient(path);
            blobClient.upload(content, length, true);
            // We could set http headers/content type if needed
            return blobClient.getBlobUrl();
        } catch (Exception e) {
            throw new com.drtx.ecomerce.amazon.core.model.exceptions.StorageException(
                    "Failed to upload image to Azure Storage", e);
        }
    }
}

package com.drtx.ecomerce.amazon.core.ports.out;

import java.io.InputStream;

public interface ImageStoragePort {
    String uploadImage(String fileName, InputStream content, long length, String contentType);
}

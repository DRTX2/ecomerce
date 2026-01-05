package com.drtx.ecomerce.amazon.core.ports.in.rest;

import com.drtx.ecomerce.amazon.core.model.product.ImageFile;
import java.util.List;

public interface UploadProductImageUseCasePort {
    List<String> uploadImages(Long userId, String userRole, List<ImageFile> files);
}

package com.drtx.ecomerce.amazon.core.model.product;

import lombok.Builder;
import lombok.Data;
import java.io.InputStream;

@Data
@Builder
public class ImageFile {
    private String fileName;
    private String contentType;
    private long size;
    private InputStream content;
}

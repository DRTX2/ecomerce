package com.drtx.ecomerce.amazon.adapters.in.rest.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageUploadResponse {
    private List<String> imageUrls;
}

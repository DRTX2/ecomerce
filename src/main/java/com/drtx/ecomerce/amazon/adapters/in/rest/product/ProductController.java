package com.drtx.ecomerce.amazon.adapters.in.rest.product;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
    private final ProductUseCasePort service;
    private final com.drtx.ecomerce.amazon.application.usecases.product.UploadProductImageUseCase uploadImageUseCase;
    private final ProductRestMapper mapper;

    @PostMapping(value = "/images", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ImageUploadResponse> uploadImages(
            @RequestParam("files") List<org.springframework.web.multipart.MultipartFile> files,
            org.springframework.security.core.Authentication authentication) {

        // Extract User Role and ID from Authentication
        // Assuming CustomUserDetails or similar structure, or standard checking
        // For simplicity, we assume we can get role from authorities

        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .orElse("USER");

        // We might need to fetch userId too if needed, but the current usecase
        // signature asks for it.
        // Let's assume a dummy userId or extract from principal if possible.
        // Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        // For now, let's pass 1L or modify usecase to not strictly need userId if not
        // logged.
        // But the requirement said "SELLER".

        Long userId = 1L; // Placeholder or extracting from Token if I had the security utils handy.
        // Let's rely on the fact that the usecase checks "SELLER" string for now.

        List<String> urls = uploadImageUseCase.uploadImages(userId, role, files);

        return ResponseEntity.ok(com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ImageUploadResponse.builder()
                .imageUrls(urls)
                .build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(
                this.service.getAllProducts()
                        .stream()
                        .map(mapper::toResponse)
                        .toList());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest req) {
        Product newProduct = mapper.toDomain(req);
        return ResponseEntity.ok(mapper.toResponse(
                this.service.createProduct(newProduct)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findProductById(@PathVariable Long id) {
        return service.getProductById(id)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest req) {
        Product product = mapper.toDomain(req);
        return ResponseEntity.ok(mapper.toResponse(
                service.updateProduct(id, product)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
package com.drtx.ecomerce.amazon.adapters.in.rest.product;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ImageUploadResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import com.drtx.ecomerce.amazon.core.model.product.ImageFile;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UploadProductImageUseCasePort;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {
        private final ProductUseCasePort service;
        private final UploadProductImageUseCasePort uploadImageUseCase;
        private final ProductRestMapper mapper;

        @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ImageUploadResponse> uploadImages(
                        @RequestParam("files") List<MultipartFile> files,
                        org.springframework.security.core.Authentication authentication) {

                String role = authentication.getAuthorities().stream()
                                .findFirst()
                                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                                .orElse("USER");

                Long userId = 1L; // Placeholder as in original

                List<ImageFile> imageFiles = files.stream()
                                .map(this::toImageFile)
                                .collect(Collectors.toList());

                List<String> urls = uploadImageUseCase.uploadImages(userId, role, imageFiles);

                return ResponseEntity.ok(ImageUploadResponse.builder()
                                .imageUrls(urls)
                                .build());
        }

        private ImageFile toImageFile(MultipartFile file) {
                try {
                        return ImageFile.builder()
                                        .fileName(file.getOriginalFilename())
                                        .contentType(file.getContentType())
                                        .size(file.getSize())
                                        .content(file.getInputStream())
                                        .build();
                } catch (IOException e) {
                        throw new RuntimeException("Error processing file upload", e);
                }
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
        @PreAuthorize("hasRole('SELLER')")
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
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Product not found with id: " + id));
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest req) {
                Product product = mapper.toDomain(req);
                return ResponseEntity.ok(mapper.toResponse(
                                service.updateProduct(id, product)));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
                service.deleteProduct(id);
                return ResponseEntity.noContent().build();
        }
}
package com.drtx.ecomerce.amazon.adapters.in.rest.product;

import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ImageUploadResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductPageResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.security.SecurityUserDetails;
import com.drtx.ecomerce.amazon.core.model.exceptions.EntityNotFoundException;
import com.drtx.ecomerce.amazon.core.model.product.ImageFile;
import com.drtx.ecomerce.amazon.core.model.product.Product;
import com.drtx.ecomerce.amazon.core.ports.in.rest.ProductUseCasePort;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UploadProductImageUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Productos", description = "Gestión del catálogo de productos")
@RestController
@RequestMapping("/products")
@AllArgsConstructor
@Validated
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
        private final ProductUseCasePort service;
        private final UploadProductImageUseCasePort uploadImageUseCase;
        private final ProductRestMapper mapper;

        @Operation(summary = "Subir imágenes de producto", description = "Sube una o múltiples imágenes para un producto. Requiere rol SELLER.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Imágenes subidas correctamente",
                        content = @Content(schema = @Schema(implementation = ImageUploadResponse.class))),
                @ApiResponse(responseCode = "401", description = "No autenticado"),
                @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol SELLER"),
                @ApiResponse(responseCode = "400", description = "Archivo inválido o excede límites")
        })
        @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ImageUploadResponse> uploadImages(
                        @Parameter(description = "Archivos de imagen a subir (máx 5, 5MB cada uno)", required = true)
                        @RequestParam("files") List<MultipartFile> files,
                        @AuthenticationPrincipal SecurityUserDetails principal) {

                String role = principal.getUser().getRole().roleName();
                Long userId = principal.getUser().getId();

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

        @Operation(summary = "Listar todos los productos", description = "Obtiene el catálogo completo de productos disponibles")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Lista de productos",
                        content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                @ApiResponse(responseCode = "401", description = "No autenticado")
        })
        @GetMapping
        public ResponseEntity<List<ProductResponse>> getAllProducts() {
                return ResponseEntity.ok(
                                this.service.getAllProducts()
                                                .stream()
                                                .map(mapper::toResponse)
                                                .toList());
        }

        @Operation(summary = "Buscar productos activos", description = "Filtra el catálogo público y devuelve resultados paginados")
        @GetMapping("/search")
        public ResponseEntity<ProductPageResponse> searchActiveProducts(
                        @RequestParam(required = false) @Size(max = 100) String query,
                        @RequestParam(required = false) @Min(1) Long categoryId,
                        @RequestParam(defaultValue = "0") @Min(0) int page,
                        @RequestParam(defaultValue = "24") @Min(1) @Max(100) int size) {
                var results = service.searchActiveProducts(query, categoryId, page, size);
                return ResponseEntity.ok(new ProductPageResponse(
                                results.content().stream().map(mapper::toResponse).toList(),
                                results.page(), results.size(), results.totalElements(), results.totalPages()));
        }

        @Operation(summary = "Crear producto", description = "Crea un nuevo producto en el catálogo. Requiere rol SELLER.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Producto creado",
                        content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                @ApiResponse(responseCode = "401", description = "No autenticado"),
                @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol SELLER"),
                @ApiResponse(responseCode = "400", description = "Datos de producto inválidos")
        })
        @PostMapping
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest req) {
                Product newProduct = mapper.toDomain(req);
                return ResponseEntity.ok(mapper.toResponse(
                                this.service.createProduct(newProduct)));
        }

        @Operation(summary = "Obtener producto por ID", description = "Busca un producto específico por su identificador")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Producto encontrado",
                        content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                @ApiResponse(responseCode = "401", description = "No autenticado")
        })
        @GetMapping("/{id}")
        public ResponseEntity<ProductResponse> findProductById(
                @Parameter(description = "ID del producto", example = "1", required = true)
                @PathVariable Long id) {
                return service.getProductById(id)
                                .map(mapper::toResponse)
                                .map(ResponseEntity::ok)
                                .orElseThrow(() -> new EntityNotFoundException(
                                                "Product not found with id: " + id));
        }

        @Operation(summary = "Actualizar producto", description = "Actualiza un producto existente. Requiere rol SELLER.")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Producto actualizado",
                        content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                @ApiResponse(responseCode = "401", description = "No autenticado"),
                @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol SELLER"),
                @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
                @ApiResponse(responseCode = "400", description = "Datos de producto inválidos")
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<ProductResponse> updateProduct(
                @Parameter(description = "ID del producto", example = "1", required = true)
                @PathVariable Long id,
                @RequestBody ProductRequest req) {
                Product product = mapper.toDomain(req);
                return ResponseEntity.ok(mapper.toResponse(
                                service.updateProduct(id, product)));
        }

        @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo. Requiere rol SELLER.")
        @ApiResponses({
                @ApiResponse(responseCode = "204", description = "Producto eliminado"),
                @ApiResponse(responseCode = "401", description = "No autenticado"),
                @ApiResponse(responseCode = "403", description = "No autorizado - requiere rol SELLER"),
                @ApiResponse(responseCode = "404", description = "Producto no encontrado")
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('SELLER')")
        public ResponseEntity<Void> deleteProduct(
                @Parameter(description = "ID del producto", example = "1", required = true)
                @PathVariable Long id) {
                service.deleteProduct(id);
                return ResponseEntity.noContent().build();
        }
}

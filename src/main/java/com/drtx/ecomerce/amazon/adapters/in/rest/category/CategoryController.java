package com.drtx.ecomerce.amazon.adapters.in.rest.category;

import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.dto.CategoryResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.category.mappers.CategoryRestMapper;
import com.drtx.ecomerce.amazon.core.model.product.Category;
import com.drtx.ecomerce.amazon.core.ports.in.rest.CategoryUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categorías", description = "Gestión de categorías de productos")
@RestController
@RequestMapping("/categories")
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoryUseCasePort categoryUseCasePort;
    private final CategoryRestMapper categoryMapper;

    @Operation(summary = "Listar todas las categorías", description = "Obtiene el listado completo de categorías")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de categorías",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryUseCasePort.getAllCategories();
        return ResponseEntity.ok(
                categories.stream().map(categoryMapper::toResponse).toList());
    }

    @Operation(summary = "Crear categoría", description = "Crea una nueva categoría. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría creada",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "400", description = "Datos de categoría inválidos")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody @jakarta.validation.Valid CategoryRequest category) {
        Category newCategory = categoryMapper.toDomain(category);
        return ResponseEntity.ok(categoryMapper.toResponse(
                categoryUseCasePort.createCategory(newCategory)));
    }

    @Operation(summary = "Obtener categoría por ID", description = "Busca una categoría específica por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "ID de la categoría", example = "1", required = true)
            @PathVariable Long id) {
        return categoryUseCasePort.getCategoryById(id).map(categoryMapper::toResponse).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar categoría", description = "Actualiza una categoría existente. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada"),
            @ApiResponse(responseCode = "400", description = "Datos de categoría inválidos")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(description = "ID de la categoría", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody @jakarta.validation.Valid CategoryRequest categoryRequest) {
        Category category = categoryMapper.toDomain(categoryRequest);
        return ResponseEntity.ok(categoryMapper.toResponse(
                categoryUseCasePort.updateCategory(id, category)));
    }

    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID de la categoría", example = "1", required = true)
            @PathVariable Long id) {
        categoryUseCasePort.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}

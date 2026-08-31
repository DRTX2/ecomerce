package com.drtx.ecomerce.amazon.adapters.in.rest.favorite;

import com.drtx.ecomerce.amazon.adapters.in.rest.favorite.dto.FavoriteResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.favorite.mappers.FavoriteRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.ports.in.rest.FavoriteUseCasePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Favoritos", description = "Gestión de lista de deseos/favoritos del usuario")
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FavoriteController {

    private final FavoriteUseCasePort favoriteUseCase;
    private final FavoriteRestMapper favoriteMapper;
    private final ProductRestMapper productMapper;

    @Operation(summary = "Agregar producto a favoritos", description = "Añade un producto a la lista de favoritos del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto agregado a favoritos",
                    content = @Content(schema = @Schema(implementation = FavoriteResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Producto ya en favoritos")
    })
    @PostMapping("/product/{productId}")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long productId) {
        String userEmail = getAuthenticatedUserEmail();
        Favorite favorite = favoriteUseCase.addFavorite(productId, userEmail);
        return ResponseEntity.ok(favoriteMapper.toResponse(favorite));
    }

    @Operation(summary = "Eliminar producto de favoritos", description = "Quita un producto de la lista de favoritos del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado de favoritos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado en favoritos")
    })
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Long productId) {
        String userEmail = getAuthenticatedUserEmail();
        favoriteUseCase.removeFavorite(productId, userEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Listar favoritos del usuario", description = "Obtiene todos los productos en la lista de favoritos del usuario autenticado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de productos favoritos",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getUserFavorites() {
        String userEmail = getAuthenticatedUserEmail();
        List<ProductResponse> favorites = favoriteUseCase.getUserFavorites(userEmail).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(favorites);
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }
        return null;
    }
}

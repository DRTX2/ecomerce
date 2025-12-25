package com.drtx.ecomerce.amazon.adapters.in.rest.favorite;

import com.drtx.ecomerce.amazon.adapters.in.rest.favorite.dto.FavoriteResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.favorite.mappers.FavoriteRestMapper;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.dto.ProductResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.product.mappers.ProductRestMapper;
import com.drtx.ecomerce.amazon.core.model.user.Favorite;
import com.drtx.ecomerce.amazon.core.ports.in.rest.FavoriteUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteUseCasePort favoriteUseCase;
    private final FavoriteRestMapper favoriteMapper;
    private final ProductRestMapper productMapper;

    @PostMapping("/product/{productId}")
    public ResponseEntity<FavoriteResponse> addFavorite(@PathVariable Long productId) {
        String userEmail = getAuthenticatedUserEmail();
        Favorite favorite = favoriteUseCase.addFavorite(productId, userEmail);
        return ResponseEntity.ok(favoriteMapper.toResponse(favorite));
    }

    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Long productId) {
        String userEmail = getAuthenticatedUserEmail();
        favoriteUseCase.removeFavorite(productId, userEmail);
        return ResponseEntity.noContent().build();
    }

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

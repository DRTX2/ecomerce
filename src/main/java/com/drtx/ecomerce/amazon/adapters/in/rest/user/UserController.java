package com.drtx.ecomerce.amazon.adapters.in.rest.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UserUseCasePort;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserUseCasePort userUseCasePort;
    private final UserRestMapper userMapper;

    @Operation(summary = "Listar todos los usuarios", description = "Obtiene todos los usuarios del sistema. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de usuarios",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere ADMIN")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<User> users= userUseCasePort.getAllUsers();
        List<UserResponse> listUserResponse=users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok( listUserResponse);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario específico por su identificador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.user.id or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> findById(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long id){
        return userUseCasePort.getUserById(id)
                .map(userMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos")
    })
    @PutMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.user.id or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long id,
            @RequestBody UserRequest request){
        User user =userMapper.toDomain(request);
        return ResponseEntity.ok(userMapper.toResponse(userUseCasePort.updateUser(id,user)));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema. Requiere rol ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado - requiere ADMIN"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Long id){
        userUseCasePort.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

package com.drtx.ecomerce.amazon.adapters.in.rest.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.user.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UserUseCasePort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserUseCasePort userUseCasePort;
    private final UserRestMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<User> users= userUseCasePort.getAllUsers();
        List<UserResponse> listUserResponse=users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok( listUserResponse);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> findByUuid(@PathVariable UUID uuid){
        return userUseCasePort.getUserByUuid(uuid)
                .map(userMapper::toResponse)
               .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{uuid}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID uuid, @RequestBody UserRequest request){
        User user = userMapper.toDomain(request);
        return ResponseEntity.ok(userMapper.toResponse(userUseCasePort.updateUserByUuid(uuid, user)));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid){
        userUseCasePort.deleteUserByUuid(uuid);
        return ResponseEntity.noContent().build();
    }
}

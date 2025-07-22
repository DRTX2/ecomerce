package com.drtx.ecomerce.amazon.adapters.in.rest.user;

import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.in.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.model.User;
import com.drtx.ecomerce.amazon.core.ports.in.rest.UserUseCasePort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/")
public class UserController {
    private final UserUseCasePort userUseCasePort;
    private final UserRestMapper userMapper;

    public UserController(UserUseCasePort userUseCasePort, UserRestMapper userMapper) {
        this.userUseCasePort = userUseCasePort;
        this.userMapper = userMapper;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<User> users= userUseCasePort.getAllUsers();
        List<UserResponse> listUserResponse=users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok( listUserResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id){
        return userUseCasePort.getUserById(id)
                .map(userMapper::toResponse)
               .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request){
        User user = userMapper.toDomain(request);
        return ResponseEntity.ok(userMapper.toResponse(userUseCasePort.createUser(user) ) );
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, UserRequest request){
        User user =userMapper.toDomain(request);
        return ResponseEntity.ok(userMapper.toResponse(userUseCasePort.updateUser(id,user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userUseCasePort.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

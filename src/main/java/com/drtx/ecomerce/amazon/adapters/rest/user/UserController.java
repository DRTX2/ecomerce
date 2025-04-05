package com.drtx.ecomerce.amazon.adapters.rest.user;

import com.drtx.ecomerce.amazon.adapters.rest.user.dto.UserRequest;
import com.drtx.ecomerce.amazon.adapters.rest.user.dto.UserResponse;
import com.drtx.ecomerce.amazon.adapters.rest.user.mappers.UserRestMapper;
import com.drtx.ecomerce.amazon.core.models.User;
import com.drtx.ecomerce.amazon.core.usecases.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/")
public class UserController {
    private final UserService userService;
    private final UserRestMapper userMapper;

    public UserController(UserService userService, UserRestMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }
    //getOne
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id){
        return userService.getUserById(id)
                .map(userMapper::toResponse)
               .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request){
        User user = userMapper.toDomain(request);
        return ResponseEntity.ok(userMapper.toResponse(userService.createUser(user) ) );
    }
    //update

    //getAll

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

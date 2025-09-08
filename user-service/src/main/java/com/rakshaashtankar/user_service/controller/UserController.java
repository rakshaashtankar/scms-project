package com.rakshaashtankar.user_service.controller;

import com.rakshaashtankar.user_service.dto.*;
import com.rakshaashtankar.user_service.model.User;
import com.rakshaashtankar.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping()
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        return userService.createUser(userCreateRequest);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = userService.updateUser(id, userUpdateRequest);
        if(updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }


    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id, @Valid @RequestBody UserPatchRequest userPatchRequest) {
        UserResponse patchUpdatedUser = userService.patchUser(id, userPatchRequest);
        if(patchUpdatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patchUpdatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if(userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id,
                                               @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            userService.changePassword(id, passwordChangeRequest);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // 400 Bad Request
        }
    }

}

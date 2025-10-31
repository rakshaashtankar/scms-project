package com.rakshaashtankar.user_service.controller;

import com.rakshaashtankar.user_service.dto.*;
import com.rakshaashtankar.user_service.exception.ResourceNotFoundException;
import com.rakshaashtankar.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private final UserService userService;

    //Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<UserResponse> users= userService.getAllUsers();
        if(users.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No records added yet.");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'FACULTY')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " +id));
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        UserResponse response = userService.createUser(userCreateRequest);
        return ResponseEntity
                .created(URI.create("/api/users/" + response.getId())) // 🔹 Return 201 Created
                .body(response);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateRequest));
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'FACULTY')")
    public ResponseEntity<UserResponse> patchUser(@PathVariable Long id, @Valid @RequestBody UserPatchRequest userPatchRequest) {
        return ResponseEntity.ok(userService.patchUser(id, userPatchRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'FACULTY')")
    public ResponseEntity<String> changePassword(@PathVariable Long id,
                                               @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        String response = userService.changePassword(id, passwordChangeRequest);
        return ResponseEntity.ok(response); // 200 OK with success message
    }

}

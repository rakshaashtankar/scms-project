package com.rakshaashtankar.user_service.mapper;

import com.rakshaashtankar.user_service.dto.UserCreateRequest;
import com.rakshaashtankar.user_service.dto.UserResponse;
import com.rakshaashtankar.user_service.dto.UserUpdateRequest;
import com.rakshaashtankar.user_service.model.User;

public class UserMapper {

    public static User toEntity(UserCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole().toUpperCase());
        user.setPassword(request.getPassword());
        return  user;
    }

    public static void updateEntity(User user, UserUpdateRequest request) {
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername().trim());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail().trim());
        }
        if (request.getRole() != null) {
            user.setRole(request.getRole().toUpperCase());
        }
        if (request.getPassword() != null) {
            user.setPassword(request.getPassword());
        }
    }

    public static UserResponse toResponse(User user) {
        return  new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isPasswordChanged()
        );
    }
}

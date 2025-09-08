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
        user.setPassword(request.getUsername()+"1234");
        user.setRole(request.getRole().toUpperCase());
        return  user;
    }

    public static void updateEntity(User user, UserUpdateRequest request) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getUsername()+"1234");
        user.setRole(request.getRole());
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

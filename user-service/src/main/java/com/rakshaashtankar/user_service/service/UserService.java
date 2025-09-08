package com.rakshaashtankar.user_service.service;

import com.rakshaashtankar.user_service.dto.*;
import com.rakshaashtankar.user_service.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();
    Optional<UserResponse> getUserById(Long id);
    UserResponse createUser(UserCreateRequest userCreateRequest);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);
    UserResponse patchUser(Long id, UserPatchRequest userPatchRequest);
    boolean deleteUser(Long id);
    void changePassword(Long id, PasswordChangeRequest passwordChangeRequest);
}

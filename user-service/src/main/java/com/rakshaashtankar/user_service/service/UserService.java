package com.rakshaashtankar.user_service.service;

import com.rakshaashtankar.user_service.dto.PasswordChangeRequest;
import com.rakshaashtankar.user_service.dto.UserCreateRequest;
import com.rakshaashtankar.user_service.dto.UserPatchRequest;
import com.rakshaashtankar.user_service.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(UserCreateRequest request);
    User updateUser(Long id, User user);
    User patchUser(Long id, UserPatchRequest patchRequest);
    boolean deleteUser(Long id);
    void changePassword(Long id, PasswordChangeRequest request);
}

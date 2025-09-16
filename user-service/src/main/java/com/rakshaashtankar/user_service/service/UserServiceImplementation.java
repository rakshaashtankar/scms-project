package com.rakshaashtankar.user_service.service;

import com.rakshaashtankar.user_service.dto.*;
import com.rakshaashtankar.user_service.exception.InvalidRequestException;
import com.rakshaashtankar.user_service.exception.PasswordMismatchException;
import com.rakshaashtankar.user_service.exception.ResourceNotFoundException;
import com.rakshaashtankar.user_service.mapper.UserMapper;
import com.rakshaashtankar.user_service.model.User;
import com.rakshaashtankar.user_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements  UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream().map(UserMapper::toResponse).toList();
    }

    @Override
    public Optional<UserResponse> getUserById(Long id) {
        return userRepository.findById(id).map(UserMapper::toResponse);
    }

    @Override
    public UserResponse createUser(UserCreateRequest userCreateRequest) {
        StringBuilder errorMessage = new StringBuilder();
        if(userRepository.existsByEmail(userCreateRequest.getEmail())) {
            errorMessage.append("Email already in use: ").append(userCreateRequest.getEmail()).append(" .");
        }
        if(userRepository.existsByUsername(userCreateRequest.getUsername())) {
            errorMessage.append("Username already in use: ").append(userCreateRequest.getUsername()).append(" .");
        }
        if(errorMessage.length() > 0) {
            throw new InvalidRequestException(errorMessage.toString().trim());
        }
        User newUser = UserMapper.toEntity(userCreateRequest);
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        User savedUser = userRepository.save(newUser);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse patchUser(Long id, UserPatchRequest userPatchRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " +id));

        if (userPatchRequest.getEmail() != null && !userPatchRequest.getEmail().trim().isEmpty()) {
            String newEmail = userPatchRequest.getEmail().trim();
            if (userRepository.existsByEmail(newEmail) && !newEmail.equals(existingUser.getEmail())) {
                // 🔹 Improved uniqueness check
                throw new InvalidRequestException("Email already exists: " + newEmail);
            }
            existingUser.setEmail(newEmail);
        }

        User savedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " +id));
        UserMapper.updateEntity(existingUser, userUpdateRequest);
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
            existingUser.setPasswordChanged(true);
        }
        User savedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(savedUser);

    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }

    @Override
    public String changePassword(Long id, PasswordChangeRequest passwordChangeRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        if(!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            throw  new PasswordMismatchException("Old password does not match");
        }
        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        user.setPasswordChanged(true);
        userRepository.save(user);
        return "Password Changed successfully";
    }
}

package com.rakshaashtankar.user_service.service;

import com.rakshaashtankar.user_service.dto.*;
import com.rakshaashtankar.user_service.exception.InvalidRequestException;
import com.rakshaashtankar.user_service.exception.PasswordMismatchException;
import com.rakshaashtankar.user_service.exception.ResourceNotFoundException;
import com.rakshaashtankar.user_service.mapper.UserMapper;
import com.rakshaashtankar.user_service.model.User;
import com.rakshaashtankar.user_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImplementation implements  UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        if(!errorMessage.isEmpty()) {
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
            String existingEmail = existingUser.getEmail() != null ? existingUser.getEmail().trim() : "";

            if (newEmail.equals(existingEmail)) {
                throw new InvalidRequestException("Email is already set to this value: " + existingEmail);
            }

            existingUser.setEmail(newEmail);
        }
        if(userPatchRequest.getPassword() != null && !userPatchRequest.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userPatchRequest.getPassword()));
            existingUser.setPasswordChanged(true);
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

package com.rakshaashtankar.user_service.service;

import com.rakshaashtankar.user_service.dto.*;
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
        User newUser = UserMapper.toEntity(userCreateRequest);
        String defaultPassword = newUser.getPassword();
        newUser.setPassword(passwordEncoder.encode(defaultPassword));
        User savedUser = userRepository.save(newUser);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id : " +id));
        UserMapper.updateEntity(existingUser, userUpdateRequest);
        if(userUpdateRequest.getPassword()!= null) {
            existingUser.setPassword(passwordEncoder.encode(existingUser.getPassword()));
        }
        User updateUser = userRepository.save(existingUser);
        return UserMapper.toResponse(updateUser) ;
    }

    @Override
    public UserResponse patchUser(Long id, UserPatchRequest userPatchRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id : " +id));
        if(userPatchRequest.getEmail() != null) existingUser.setEmail(userPatchRequest.getEmail());
        if(userPatchRequest.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userPatchRequest.getPassword()));
            existingUser.setPasswordChanged(true);
        }
        User savedUser = userRepository.save(existingUser);
        return UserMapper.toResponse(savedUser);
    }

    @Override
    public boolean deleteUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public void changePassword(Long id, PasswordChangeRequest passwordChangeRequest) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if(!passwordEncoder.matches(passwordChangeRequest.getOldPassword(), user.getPassword())) {
            throw  new RuntimeException("Old password does not match");
        }
        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        user.setPasswordChanged(true);
        userRepository.save(user);
    }
}

package com.petadoption.system.service;

import com.petadoption.system.dto.AuthRequest;
import com.petadoption.system.dto.AuthResponse;
import com.petadoption.system.dto.RegisterRequest;
import com.petadoption.system.exception.BadRequestException;
import com.petadoption.system.exception.ResourceNotFoundException;
import com.petadoption.system.model.User;
import com.petadoption.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // In simple version, we store password as-is
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setRole(request.getRole());

        // Save to database
        User savedUser = userRepository.save(user);

        // Return response (no token needed in simple version)
        return new AuthResponse(
                null, // No token
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    /**
     * Login - Simple version: just check if email and password match
     */
    public AuthResponse login(AuthRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        // Check if password matches (simple comparison)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new BadRequestException("Invalid email or password");
        }

        // Return user details (no token)
        return new AuthResponse(
                null, // No token
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }

    /**
     * Get user by ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Get all users (for admin)
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Update user profile
     */
    public User updateUser(Long id, User updatedUser) {
        User user = getUserById(id);

        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());
        user.setAddress(updatedUser.getAddress());

        return userRepository.save(user);
    }
}

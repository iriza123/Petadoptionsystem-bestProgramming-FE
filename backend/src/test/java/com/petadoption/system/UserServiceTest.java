package com.petadoption.system;

import com.petadoption.system.dto.AuthRequest;
import com.petadoption.system.dto.AuthResponse;
import com.petadoption.system.dto.RegisterRequest;
import com.petadoption.system.exception.BadRequestException;
import com.petadoption.system.exception.ResourceNotFoundException;
import com.petadoption.system.model.User;
import com.petadoption.system.repository.UserRepository;
import com.petadoption.system.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 * Tests cover registration, login, user retrieval, and update operations.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        // Set up a reusable test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@doghaus.rw");
        testUser.setPassword("password123");
        testUser.setRole(User.Role.ADOPTER);
        testUser.setPhone("0781234567");
        testUser.setAddress("Kimironko, Kigali");

        // Set up a reusable register request
        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@doghaus.rw");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("0781234567");
        registerRequest.setAddress("Kimironko, Kigali");
        registerRequest.setRole(User.Role.ADOPTER);

        // Set up a reusable auth request
        authRequest = new AuthRequest();
        authRequest.setEmail("john@doghaus.rw");
        authRequest.setPassword("password123");
    }

    // ===================== REGISTER TESTS =====================

    @Test
    @DisplayName("Register - Success: new user is registered successfully")
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        AuthResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@doghaus.rw", response.getEmail());
        assertEquals(User.Role.ADOPTER, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Register - Fail: duplicate email throws BadRequestException")
    void register_DuplicateEmail_ThrowsBadRequestException() {
        when(userRepository.existsByEmail("john@doghaus.rw")).thenReturn(true);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userService.register(registerRequest)
        );

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Register - Admin role is assigned correctly")
    void register_AdminRole_AssignedCorrectly() {
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setName("Admin Staff");
        adminUser.setEmail("admin@doghaus.rw");
        adminUser.setPassword("admin123");
        adminUser.setRole(User.Role.ADMIN);

        RegisterRequest adminRequest = new RegisterRequest();
        adminRequest.setName("Admin Staff");
        adminRequest.setEmail("admin@doghaus.rw");
        adminRequest.setPassword("admin123");
        adminRequest.setRole(User.Role.ADMIN);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(adminUser);

        AuthResponse response = userService.register(adminRequest);

        assertEquals(User.Role.ADMIN, response.getRole());
    }

    // ===================== LOGIN TESTS =====================

    @Test
    @DisplayName("Login - Success: valid credentials return AuthResponse")
    void login_Success() {
        when(userRepository.findByEmail("john@doghaus.rw")).thenReturn(Optional.of(testUser));

        AuthResponse response = userService.login(authRequest);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@doghaus.rw", response.getEmail());
    }

    @Test
    @DisplayName("Login - Fail: wrong email throws BadRequestException")
    void login_WrongEmail_ThrowsBadRequestException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userService.login(authRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }

    @Test
    @DisplayName("Login - Fail: wrong password throws BadRequestException")
    void login_WrongPassword_ThrowsBadRequestException() {
        when(userRepository.findByEmail("john@doghaus.rw")).thenReturn(Optional.of(testUser));

        AuthRequest wrongPasswordRequest = new AuthRequest();
        wrongPasswordRequest.setEmail("john@doghaus.rw");
        wrongPasswordRequest.setPassword("wrongpassword");

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userService.login(wrongPasswordRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
    }

    // ===================== GET USER TESTS =====================

    @Test
    @DisplayName("GetUserById - Success: returns user when found")
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    @DisplayName("GetUserById - Fail: throws ResourceNotFoundException when user not found")
    void getUserById_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(99L)
        );
    }

    @Test
    @DisplayName("GetAllUsers - Returns list of all users")
    void getAllUsers_ReturnsAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane Smith");
        user2.setEmail("jane@doghaus.rw");

        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    // ===================== UPDATE USER TESTS =====================

    @Test
    @DisplayName("UpdateUser - Success: user profile is updated correctly")
    void updateUser_Success() {
        User updatedData = new User();
        updatedData.setName("John Updated");
        updatedData.setPhone("0789999999");
        updatedData.setAddress("Nyamirambo, Kigali");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John Updated");
        savedUser.setPhone("0789999999");
        savedUser.setAddress("Nyamirambo, Kigali");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.updateUser(1L, updatedData);

        assertEquals("John Updated", result.getName());
        assertEquals("0789999999", result.getPhone());
        assertEquals("Nyamirambo, Kigali", result.getAddress());
    }
}

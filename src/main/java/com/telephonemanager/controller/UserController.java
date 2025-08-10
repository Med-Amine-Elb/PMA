package com.telephonemanager.controller;

import com.telephonemanager.dto.UserDto;
import com.telephonemanager.entity.User;
import com.telephonemanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "User management endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get all users", description = "Get all users with pagination and filtering (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) User.UserStatus status,
            @RequestParam(required = false) User.UserRole role) {

        try {
            Page<UserDto> users = userService.getUsers(page, limit, search, department, status, role);
            
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", users.getTotalElements());
            pagination.put("totalPages", users.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "users", users.getContent(),
                "pagination", pagination
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "FETCH_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get user by ID", description = "Get user details by ID (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            return userService.getUserById(id)
                    .map(user -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", true);
                        response.put("data", user);
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> {
                        Map<String, Object> response = new HashMap<>();
                        response.put("success", false);
                        response.put("error", Map.of(
                            "code", "NOT_FOUND",
                            "message", "User not found"
                        ));
                        return ResponseEntity.status(404).body(response);
                    });
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "FETCH_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new user", description = "Create a new user (Admin only)")
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserDto userDto) {
        try {
            UserDto createdUser = userService.createUser(userDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", createdUser);
            response.put("message", "User created successfully");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "CREATION_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update user information (Admin only)")
    public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUser(id, userDto);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", updatedUser);
            response.put("message", "User updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "UPDATE_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Delete user (Admin only)")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "DELETE_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/department/{department}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get users by department", description = "Get users filtered by department (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getUsersByDepartment(
            @PathVariable String department,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            Page<UserDto> users = userService.getUsersByDepartment(department, page, limit);
            
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", users.getTotalElements());
            pagination.put("totalPages", users.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "users", users.getContent(),
                "pagination", pagination
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "FETCH_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get users by status", description = "Get users filtered by status (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getUsersByStatus(
            @PathVariable User.UserStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            Page<UserDto> users = userService.getUsersByStatus(status, page, limit);
            
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", users.getTotalElements());
            pagination.put("totalPages", users.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "users", users.getContent(),
                "pagination", pagination
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "FETCH_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ASSIGNER')")
    @Operation(summary = "Get users by role", description = "Get users filtered by role (Admin/Assigner only)")
    public ResponseEntity<Map<String, Object>> getUsersByRole(
            @PathVariable User.UserRole role,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {

        try {
            Page<UserDto> users = userService.getUsersByRole(role, page, limit);
            
            Map<String, Object> pagination = new HashMap<>();
            pagination.put("page", page);
            pagination.put("limit", limit);
            pagination.put("total", users.getTotalElements());
            pagination.put("totalPages", users.getTotalPages());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "users", users.getContent(),
                "pagination", pagination
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", Map.of(
                "code", "FETCH_ERROR",
                "message", e.getMessage()
            ));
            return ResponseEntity.badRequest().body(response);
        }
    }
} 
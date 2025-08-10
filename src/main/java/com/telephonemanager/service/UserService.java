package com.telephonemanager.service;

import com.telephonemanager.dto.UserDto;
import com.telephonemanager.entity.User;
import com.telephonemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<UserDto> getUsers(int page, int limit, String search, String department, 
                                 User.UserStatus status, User.UserRole role) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        
        Page<User> users = userRepository.findUsersWithFilters(search, department, status, role, pageable);
        
        return users.map(UserDto::new);
    }

    public Optional<UserDto> getUserById(Long id) {
        return userRepository.findById(id).map(UserDto::new);
    }

    public UserDto createUser(UserDto userDto) {
        // Check if email already exists
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        user.setDepartment(userDto.getDepartment());
        user.setPosition(userDto.getPosition());
        user.setStatus(userDto.getStatus());
        user.setJoinDate(userDto.getJoinDate() != null ? userDto.getJoinDate() : LocalDate.now());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setManager(userDto.getManager());
        user.setAvatar(userDto.getAvatar());

        User savedUser = userRepository.save(user);
        return new UserDto(savedUser);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if email is being changed to another user's email
        if (userRepository.existsByEmailAndNotId(userDto.getEmail(), id)) {
            throw new RuntimeException("Email already exists");
        }

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        
        // Only update password if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        user.setRole(userDto.getRole());
        user.setDepartment(userDto.getDepartment());
        user.setPosition(userDto.getPosition());
        user.setStatus(userDto.getStatus());
        user.setPhone(userDto.getPhone());
        user.setAddress(userDto.getAddress());
        user.setManager(userDto.getManager());
        user.setAvatar(userDto.getAvatar());

        User updatedUser = userRepository.save(user);
        return new UserDto(updatedUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public Page<UserDto> getUsersByDepartment(String department, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> users = userRepository.findByDepartment(department, pageable);
        return users.map(UserDto::new);
    }

    public Page<UserDto> getUsersByStatus(User.UserStatus status, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> users = userRepository.findByStatus(status, pageable);
        return users.map(UserDto::new);
    }

    public Page<UserDto> getUsersByRole(User.UserRole role, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<User> users = userRepository.findByRole(role, pageable);
        return users.map(UserDto::new);
    }

    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }

    public long getUserCountByRole(User.UserRole role) {
        return userRepository.countByRole(role);
    }
} 
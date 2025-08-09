package com.telephonemanager.dto;

import com.telephonemanager.entity.User;

public class LoginResponse {

    private boolean success;
    private LoginData data;

    public LoginResponse() {}

    public LoginResponse(boolean success, LoginData data) {
        this.success = success;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public static class LoginData {
        private String token;
        private UserResponse user;

        public LoginData() {}

        public LoginData(String token, UserResponse user) {
            this.token = token;
            this.user = user;
        }

        // Getters and Setters
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public UserResponse getUser() {
            return user;
        }

        public void setUser(UserResponse user) {
            this.user = user;
        }
    }

    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private User.UserRole role;
        private String department;
        private String avatar;

        public UserResponse() {}

        public UserResponse(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.department = user.getDepartment();
            this.avatar = user.getAvatar();
        }

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public User.UserRole getRole() {
            return role;
        }

        public void setRole(User.UserRole role) {
            this.role = role;
        }

        public String getDepartment() {
            return department;
        }

        public void setDepartment(String department) {
            this.department = department;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
} 
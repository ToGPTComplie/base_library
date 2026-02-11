package com.example.library.service.auth;

import com.example.library.dto.UserRegisterRequest;

public interface AuthService {
    void register(UserRegisterRequest userRegisterRequest);
}

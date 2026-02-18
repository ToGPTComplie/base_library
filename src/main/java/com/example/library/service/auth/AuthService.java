package com.example.library.service.auth;

import com.example.library.dto.TokenRefreshResponse;
import com.example.library.dto.UserLoginRequest;
import com.example.library.dto.UserRegisterRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    void register(UserRegisterRequest userRegisterRequest);

    TokenRefreshResponse login(UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest);

}

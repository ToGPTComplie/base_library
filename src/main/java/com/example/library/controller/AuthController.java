package com.example.library.controller;

import com.example.library.common.Result;
import com.example.library.dto.TokenRefreshRequest;
import com.example.library.dto.TokenRefreshResponse;
import com.example.library.dto.UserLoginRequest;
import com.example.library.dto.UserRegisterRequest;
import com.example.library.service.auth.AuthService;
import com.example.library.service.auth.RefreshTokenService;
import com.example.library.util.DeviceIdResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final DeviceIdResolver deviceIdResolver;

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        authService.register(userRegisterRequest);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<TokenRefreshResponse> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        TokenRefreshResponse tokenRefreshResponse = authService.login(userLoginRequest, httpServletRequest);
        return Result.success(userLoginRequest.getUsername()+"登录成功", tokenRefreshResponse);
    }

    @PostMapping("/refresh-token")
    public Result<TokenRefreshResponse> refreshToken(@RequestBody @Valid TokenRefreshRequest request, HttpServletRequest httpServletRequest) {
        String requestRefreshToken = request.getRefreshToken();
        String deviceId = deviceIdResolver.resolveDeviceId(httpServletRequest);
        TokenRefreshResponse tokenRefreshResponse = refreshTokenService.refreshToken(requestRefreshToken, deviceId);
        return Result.success(tokenRefreshResponse);

    }

}

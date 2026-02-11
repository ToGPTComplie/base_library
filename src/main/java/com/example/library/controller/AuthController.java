package com.example.library.controller;

import com.example.library.common.Result;
import com.example.library.dto.UserRegisterRequest;
import com.example.library.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        authService.register(userRegisterRequest);
        return Result.success();
    }
}

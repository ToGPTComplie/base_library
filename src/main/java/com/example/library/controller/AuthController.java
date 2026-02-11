package com.example.library.controller;

import com.example.library.common.Result;
import com.example.library.dto.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    @PostMapping("/register")
    public Result<String> register(@RequestBody UserRegisterRequest userRegisterRequest) {

    }
}

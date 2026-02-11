package com.example.library.service.auth.impl;

import com.example.library.common.ResultCode;
import com.example.library.dto.UserRegisterRequest;
import com.example.library.entity.User;
import com.example.library.exception.AuthException;
import com.example.library.exception.CommonException;
import com.example.library.mapper.UserMapper;
import com.example.library.repository.UserRepository;
import com.example.library.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void register(UserRegisterRequest userRegisterRequest) {
        List<User> existingUsers = userMapper.selectByRegisterInfo(userRegisterRequest);

        if (!existingUsers.isEmpty()) {
            User existingUser = existingUsers.get(0);
            Map<String, String> errors = new HashMap<>();

            if (existingUser.getUsername().equals(userRegisterRequest.getUsername())) {
                errors.put("username", "用户名'" + userRegisterRequest.getUsername() + "'已被注册");
            }

            if (existingUser.getMobile() != null && existingUser.getMobile().equals(userRegisterRequest.getMobile())) {
                errors.put("mobile", "手机号'" + userRegisterRequest.getMobile() + "'已被注册");
            }

            if (existingUser.getEmail() != null && existingUser.getEmail().equals(userRegisterRequest.getEmail())) {
                errors.put("email", "邮箱'" + userRegisterRequest.getEmail() + "'已被注册");
            }

            if (!errors.isEmpty()) {
                throw new AuthException(ResultCode.USER_ALREADY_EXIST, errors);
            }

            throw new CommonException(ResultCode.FAILED);
        }

        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(userRegisterRequest.getPassword()));
        user.setEmail(userRegisterRequest.getEmail());
        user.setMobile(userRegisterRequest.getMobile());
        user.setNickname(userRegisterRequest.getNickname());

        userRepository.save(user);
    }

    
}

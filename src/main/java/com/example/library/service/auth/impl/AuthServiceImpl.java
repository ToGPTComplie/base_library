package com.example.library.service.auth.impl;

import com.example.library.dto.UserRegisterRequest;
import com.example.library.entity.User;
import com.example.library.exception.AuthException;
import com.example.library.mapper.UserMapper;
import com.example.library.repository.UserRepository;
import com.example.library.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public void register(UserRegisterRequest userRegisterRequest) {
        List<User> existingUsers = userMapper.selectByRegisterInfo(userRegisterRequest);

        if (!existingUsers.isEmpty()) {
            User existingUser = existingUsers.get(0);
            AuthException authException = new AuthException(ResultCode.USER_ALREADY_EXIST);
            if (existingUser.getUsername().equals(userRegisterRequest.getUsername())){
                authException.setData("用户名'"+userRegisterRequest.getUsername()+"'已被注册");
                throw authException;
            }

            if (existingUser.getMobile() != null && existingUser.getMobile().equals(userRegisterRequest.getMobile())){
                authException.setData("手机号'"+userRegisterRequest.getMobile()+"'已被注册");
                throw authException;
            }

            if (existingUser.getEmail() != null && existingUser.getEmail().equals(userRegisterRequest.getEmail())){
                authException.setData("邮箱'"+userRegisterRequest.getEmail()+"'已被注册");
                throw authException;
            }
            throw authException;
        }

        User user = new User();
        user.setUsername(userRegisterRequest.getUsername());
        user.setPassword(userRegisterRequest.getPassword());
        user.setEmail(userRegisterRequest.getEmail());
        user.setMobile(userRegisterRequest.getMobile());
        user.setNickname(userRegisterRequest.getNickname());

        userRepository.save(user);
    }
}

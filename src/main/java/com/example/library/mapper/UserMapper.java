package com.example.library.mapper;

import com.example.library.entity.User;

import java.util.List;

public interface UserMapper {

    List<User> selectByRegisterInfo();
}

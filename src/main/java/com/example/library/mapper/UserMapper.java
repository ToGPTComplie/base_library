package com.example.library.mapper;

import com.example.library.dto.UserRegisterRequest;
import com.example.library.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    List<User> selectByRegisterInfo(UserRegisterRequest userRegisterRequest);
}

package com.example.ewalled.app.user.service;

import com.example.ewalled.dto.UserDto;
import com.example.ewalled.entity.ServiceData;
import com.example.ewalled.entity.User;

public interface IUserService {
    ServiceData<User> create(UserDto.Create dto);
    ServiceData<String> login(UserDto.Login dto);
    ServiceData<User> me();
}

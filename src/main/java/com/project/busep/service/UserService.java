package com.project.busep.service;

import com.project.busep.dto.LoginDto;
import com.project.busep.dto.UserDto;
import com.project.busep.dto.UserTokenState;

public interface UserService {
    public UserTokenState login(LoginDto loginDto);
    public UserDto findByUsername(String username);

}
package com.project.busep.controller;

import com.project.busep.dto.LoginDto;
import com.project.busep.dto.UserDto;
import com.project.busep.dto.UserTokenState;
import com.project.busep.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "login", consumes = "application/json")
    public ResponseEntity<UserTokenState> login(@RequestBody LoginDto loginDto, HttpServletRequest request){
        return ResponseEntity.ok(userService.login(loginDto));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        UserDto userDto = userService.findByUsername(username);
        if (userDto != null) {
            return ResponseEntity.ok(userDto);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
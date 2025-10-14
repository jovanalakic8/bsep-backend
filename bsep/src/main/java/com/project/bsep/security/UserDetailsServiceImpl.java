package com.project.bsep.security;

import com.project.bsep.model.User;
import com.project.bsep.repository.UserRepository;
import jakarta.ws.rs.NotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository repository;

    public UserDetailsServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User loadUserByUsername(String username) {

        User user = repository.findByUsername(username).orElseThrow(() ->
                new NotFoundException(String.format("User does not exist, username: %s", username)));

        return user;
    }
}

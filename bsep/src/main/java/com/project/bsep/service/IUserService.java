package com.project.bsep.service;

import com.project.bsep.dto.*;
import com.project.bsep.model.Commercial;
import com.project.bsep.model.Employee;
import com.project.bsep.model.Request;
import com.project.bsep.model.User;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;

public interface IUserService {
    public Optional<User> findByUsername(String username);
    public TokenDto login(LoginDto loginDto);
    Boolean sendVerificationEmail(PasswordlessDto passwordlessDto);
    String validateVerificationToken(String token);
    TokenDto passwordlessLogin(String username);
    TokenDto refreshToken(String refreshToken);
    public ArrayList<User> findInactiveClients();
    User findById(Long id);
    User updateUser(UserProfileDto dto) throws Exception;
    ArrayList<User> findByRolesName(String roleName);
    User createUser(UserCreationDto dto) throws Exception;
    Request createRequest(RequestCreationDto dto) throws Exception;
    void deleteRequest(Long requestId) throws Exception;
    Employee findEmployeeById(Long id);
    Boolean sendMailToActivate(String id);
    Boolean validateActivationToken(String token);
    Boolean blockUser(String username, String reason);
    public ArrayList<Request> findAllRequestsNotInPast() throws ParseException;
    TokenDto verifyTotp(String username, String code);
}

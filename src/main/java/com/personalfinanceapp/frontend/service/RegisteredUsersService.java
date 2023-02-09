package com.personalfinanceapp.frontend.service;

import com.personalfinanceapp.frontend.model.Token;
import javax.servlet.http.HttpSession;
import com.personalfinanceapp.frontend.model.RegisteredUsers;
public interface RegisteredUsersService {

    Token login(String email, String password);

    RegisteredUsers addAdminToSession(String email, String token);

    Token checkToken(Integer id, String token);

    Token refreshToken(String token);

    Long logout(Integer id, String token);

}

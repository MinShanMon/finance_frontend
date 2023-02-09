package com.personalfinanceapp.frontend.model;

import java.util.Date;

import lombok.Data;

@Data
public class UserSession {
    private Token token;
    private RegisteredUsers registeredUsers;
    private Long tokentime;

    
    // private Employee

}

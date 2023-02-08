package com.personalfinanceapp.frontend.model;

import lombok.Data;

@Data
public class Token {
    String access_token;
    String refresh_token;
    String status;
}

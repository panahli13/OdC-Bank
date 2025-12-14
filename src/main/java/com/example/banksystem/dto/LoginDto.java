package com.example.banksystem.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String fullname;
    private String email;
    private String password;
}

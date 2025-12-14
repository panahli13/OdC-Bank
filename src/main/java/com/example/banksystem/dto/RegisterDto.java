package com.example.banksystem.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String fullname;
    private String fincode;
    private String email;
    private String phone;
    private String password;
}

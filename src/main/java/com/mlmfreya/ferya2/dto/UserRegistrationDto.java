package com.mlmfreya.ferya2.dto;

import lombok.Data;

@Data
public class UserRegistrationDto {
    private String name;
    private String email;
    private String mobileNumber;
    private String walletAddress;
    private String password;
    // getters, setters...
}

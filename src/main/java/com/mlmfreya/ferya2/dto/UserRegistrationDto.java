package com.mlmfreya.ferya2.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {
    private String name;
    @NotEmpty
    @Email
    private String email;
    private String mobileNumber;
    private String walletAddress;
    @NotEmpty
    @Size(min = 6, max = 15)
    private String password;
    // getters, setters...
}

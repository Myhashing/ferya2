package com.mlmfreya.ferya2.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

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

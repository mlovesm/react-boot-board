package com.example.boot.payload;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	//test
    @NotBlank
    private String usernameOrEmail;

    @NotBlank
    private String password;

}

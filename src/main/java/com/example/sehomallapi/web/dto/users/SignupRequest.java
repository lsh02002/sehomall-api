package com.example.sehomallapi.web.dto.users;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class SignupRequest {
    private String email;
    private String password;
    private String passwordConfirm;
    private String nickname;
    private String phoneNumber;
    private String address;
    private String gender;
    private String birthDate;
}

package com.certimetergroup.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private UserDTO userDTO;
    private String accessToken;
    private String refreshToken;
}

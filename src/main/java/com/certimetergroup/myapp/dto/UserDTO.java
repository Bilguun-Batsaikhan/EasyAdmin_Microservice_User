package com.certimetergroup.myapp.dto;

import com.certimetergroup.myapp.enumeration.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String firstname;
    private String surname;
    private String username;
    private String phoneNumber;
    private String email;
    private UserRoleEnum role;
    private LocalDate birthdate;
    private int age;
}

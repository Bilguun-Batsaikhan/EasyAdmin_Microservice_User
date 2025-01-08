package com.certimeter.myapp.dto;

import com.certimeter.myapp.enumeration.UserRoleEnum;
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
    private String username; //TODO: username should be unique
    private String phoneNumber;
    private String email;
    private UserRoleEnum role;
    private LocalDate birthdate;
    private int age;
}

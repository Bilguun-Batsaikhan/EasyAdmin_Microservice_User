package com.certimeter.myapp.resourcemodel;

import com.certimeter.myapp.enumeration.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor // JPA requires a no-arg constructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Username is mandatory")
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is mandatory")
    @Column(name = "password", nullable = false)
    private String password;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Role is mandatory")
    @Column(name = "role", nullable = false)
    private UserRoleEnum role;

    @Column(name = "firstname", length = 100)
    private String firstname;

    @Column(name = "surname", length = 100)
    private String surname;

    @Column(name = "birthdate")
    private LocalDate birthdate;
}

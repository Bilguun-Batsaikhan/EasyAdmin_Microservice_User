package com.certimetergroup.myapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/*The @Data annotation in Lombok is a convenient shortcut that bundles the features of:

@ToString
@EqualsAndHashCode
@Getter and @Setter
@RequiredArgsConstructor*/
@Data
@AllArgsConstructor
public class LoginRequest {
    private String username;
    private String email;
    private String password;
}

package com.certimetergroup;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashFirstUser {
    public static void main(String[] args) {
        String password = "secret";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(hashedPassword); // Save this output for the SQL insert
    }
    /*
    INSERT INTO easyadmin.users (username, password, email, phone_number, role, firstname, surname, birthdate)
VALUES (
    'Bilguun',
    '$2a$10$OD3rIIT3To6tM5lWcmYj0uAvuMQzE.AVCDRYeVX1VyZilfqPDwfEO',
    'bilguun.boss@example.com',
    '789-22-35-569',
    'SUPER_ADMIN',
    'Bilguun',
    'Batsaikhan',
    '1997-03-06'
);
*/
}

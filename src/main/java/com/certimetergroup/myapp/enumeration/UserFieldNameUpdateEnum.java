package com.certimetergroup.myapp.enumeration;

import lombok.Getter;

@Getter
public enum UserFieldNameUpdateEnum {
    USERNAME("username"),
    FIRSTNAME("firstname"),
    SURNAME("surname"),
    EMAIL("email"),
    PHONE_NUMBER("phoneNumber"),
    BIRTHDATE("birthdate"),
    ROLE("role");

    private final String fieldname;

    UserFieldNameUpdateEnum(String fieldname) {
        this.fieldname = fieldname;
    }

}

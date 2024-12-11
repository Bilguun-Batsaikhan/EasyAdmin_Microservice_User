package com.certimeter.myapp.dto;

import com.certimeter.myapp.enumeration.ResponseEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;

@Getter
@Setter
@ToString
@Builder
public class Response {
    private int code;
    private String message;

    public Response(int code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Response(ResponseEnum responseEnum) {
        super();
        this.code = responseEnum.getId();
        this.message = responseEnum.getDescription();
    }
}


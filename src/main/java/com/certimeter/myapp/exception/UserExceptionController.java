package com.certimeter.myapp.exception;

import com.certimeter.myapp.dto.Response;
import com.certimeter.myapp.enumeration.ResponseEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//This annotation is used to handle exceptions globally across all controllers in a Spring application. It allows to consolidate my exception handling logic in one place.
@RestControllerAdvice
public class UserExceptionController {
    @ExceptionHandler(FailureException.class)
    public ResponseEntity<Response> handleFailureException(FailureException exception) {
        ResponseEnum responseEnum = exception.getResponseEnum();
        if (responseEnum == ResponseEnum.AUTHORIZATION_FAILED) {
            Response failureResponse = new Response(
                    responseEnum.getId(),
                    responseEnum.getDescription()
            );
            return ResponseEntity.status(responseEnum.getHttpStatus()).body(failureResponse);
        }
        HttpStatus httpStatusOfFailure = responseEnum.getHttpStatus();
        return ResponseEntity.status(httpStatusOfFailure).body(new Response(responseEnum));
    }
}

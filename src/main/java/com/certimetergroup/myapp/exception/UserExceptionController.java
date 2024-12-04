package com.certimetergroup.myapp.exception;

import com.certimetergroup.myapp.dto.Response;
import com.certimetergroup.myapp.enumeration.ResponseEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//This annotation is used to handle exceptions globally across all controllers in a Spring application. It allows to consolidate my exception handling logic in one place.
@RestControllerAdvice
public class UserExceptionController {
    //This annotation is used to specify which exception this method will handle.
//    @ExceptionHandler(value = UserNotFoundException.class)
//    public ResponseEntity<Object> exception(UserNotFoundException exception) {
//        //This line creates a new ResponseEntity with a message “User not found” and an HTTP status code of NOT_FOUND (404). This response is sent back to the client when the exception occurs.
//        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler(value = UserAlreadyExistsException.class)
//    public ResponseEntity<Object> exception(UserAlreadyExistsException exception) {
//        return new ResponseEntity<>("User already exists", HttpStatus.CONFLICT);
//    }

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

package com.eimt.usermanagement.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class InvalidTokenException extends RuntimeException{

    public InvalidTokenException() {
    }

    public InvalidTokenException(String message) {
        super(message);
    }
}

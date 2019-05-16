package com.eimt.usermanagement.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateUserException extends RuntimeException {

    public DuplicateUserException() {
        super();
    }

    public DuplicateUserException(String message) {
        super(message);
    }
}

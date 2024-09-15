package de.elomagic.rb.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class NotAuthorizedException extends CommonRbException {

    public NotAuthorizedException() {
        super("Not authorized");
    }

}

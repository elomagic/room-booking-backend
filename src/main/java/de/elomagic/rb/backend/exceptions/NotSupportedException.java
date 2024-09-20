package de.elomagic.rb.backend.exceptions;

import jakarta.annotation.Nonnull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NotSupportedException extends CommonRbException {

    public NotSupportedException(@Nonnull String message) {
        super(message);
    }

}

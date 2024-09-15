package de.elomagic.rb.backend.exceptions;

import jakarta.annotation.Nonnull;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
public class IllegalResourceException extends CommonRbException {

    public IllegalResourceException(@Nonnull String message) {
        super(message);
    }

}

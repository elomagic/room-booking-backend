package de.elomagic.rb.backend.exceptions;

public class CommonRbException extends RuntimeException {

    public CommonRbException(String message) {
        super(message);
    }

    public CommonRbException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonRbException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

}

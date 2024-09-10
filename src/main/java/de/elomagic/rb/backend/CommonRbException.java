package de.elomagic.rb.backend;

public class CommonRbException extends RuntimeException {

    public CommonRbException(String message) {
        super(message);
    }

    public CommonRbException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommonRbException(Throwable cause) {
        super(cause);
    }

}

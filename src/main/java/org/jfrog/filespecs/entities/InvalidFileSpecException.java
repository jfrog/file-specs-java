package org.jfrog.filespecs.entities;

public class InvalidFileSpecException extends Exception {
    public InvalidFileSpecException(String message) {
        super(message);
    }

    public InvalidFileSpecException(String message, Throwable cause) {
        super(message, cause);
    }
}

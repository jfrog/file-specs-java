package org.jfrog.filespecs.entities;

import java.io.IOException;

public class InvalidFileSpecException extends IOException {
    public InvalidFileSpecException(String message) {
        super(message);
    }

    public InvalidFileSpecException(String message, Throwable cause) {
        super(message, cause);
    }
}

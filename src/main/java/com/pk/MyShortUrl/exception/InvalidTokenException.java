package com.pk.MyShortUrl.exception;

// this class extends its superclass in java (RuntimeException)
public class InvalidTokenException extends RuntimeException {
    // here the method passes the message to superclass and invokes its constructor.
    public InvalidTokenException(String message) {
        super(message);
    }

}

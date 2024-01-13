package com.pk.MyShortUrl.exception;


// extend indicates CustomException is a subclass of java class exception
public class CustomException extends Exception {
    //this line calls the constructor of superclass (Exceptions) using message super.
    public CustomException(String message) {
        super(message);
    }
    // message provides details about the exception that occurred  and is often used for debugging/logging.
}

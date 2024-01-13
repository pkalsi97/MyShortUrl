package com.pk.MyShortUrl.exception;

// lombok annotations for reduction of boilerplate code.
import lombok.AllArgsConstructor;
import lombok.Getter;

// Spring framework for HTTP request codes, exception handling and annotations
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * This class marked with @ControllerAdvice -> makes this class global exception handler for spring MCV
 * allowing ti to handel exceptions across the entire application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This method handles exceptions of type -> CustomExceptions.
     * @param ex -> was thrown.
     * @param request -> ex was thrown during this request
     * @return contains a Response entity object containing the error and HTTP status.
     * Response entity is a type of spring framework that represents the entire HTTP response including body and status
     * it is part of spring framework org.springframework.http
     */

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex, WebRequest request) {
        // creates a new ErrorDetails object -> contains the exception and web request info.
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
        // Return a response entity with the ErrorDetails and the BAD_REQUEST status.
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * This handles NullPointerException happening anywhere in the application, sets status UNAUTHORISED.
     * @param ex ->exception that was thrown.
     * @param request -> the web request during which the exception was throw.
     * @return -> returns a ResponseEntity object containing their error details and the HTTP status.
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<?> handleNullPointerException(NullPointerException ex, WebRequest request) {
        // creates a new ErrorDetails object -> contains the exception and web request info.
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.UNAUTHORIZED, "Unauthorized: " + ex.getMessage(), request.getDescription(false));
        // Return a response entity with the ErrorDetails and the UNAUTHORIZED status.
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }



    // a generic exception handler that catches all types of error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        // creates a new ErrorDetails object -> contains the exception and web request info.
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request.getDescription(false));
        // Return a response entity with the ErrorDetails and the INTERNAL_SERVER_ERROR status.
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles exceptions specifically related to invalid tokens.
     * @param ex the InvalidTokenException that was thrown.
     * @param request the web request during which the exception occurred.
     * @return a ResponseEntity object containing the error details and the HTTP status.
     */
    // handles exception specifically related to tokens used for RestFull APIs.
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex, WebRequest request) {
        // creates a new ErrorDetails object -> contains the exception and web request info.
        ErrorDetails errorDetails = new ErrorDetails(HttpStatus.UNAUTHORIZED, ex.getMessage(), request.getDescription(false));
        // Return a response entity with the ErrorDetails and the UNAUTHORIZED status
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    // Error details is a private static class tha represents details of an error
    @Getter
    @AllArgsConstructor
    private static class ErrorDetails {
        // The HTTP status code to be returned
        private final HttpStatus status;
        // A message providing details about the exception
        private final String message;
        // Additional details about the web request during which the exception occurred
        private final String details;
    }
}

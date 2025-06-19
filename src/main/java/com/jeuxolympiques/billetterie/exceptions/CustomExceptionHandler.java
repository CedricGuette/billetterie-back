package com.jeuxolympiques.billetterie.exceptions;

import com.jeuxolympiques.billetterie.configuration.HttpHeadersCORS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class CustomExceptionHandler {

    private final HttpHeadersCORS headersCORS = new HttpHeadersCORS();
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleEmailAlreadyUsedException(EmailAlreadyUsedException e) {
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.BAD_REQUEST.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(EmailPasswordInvalidException.class)
    public ResponseEntity<ApiError> handleEmailPasswordInvalidException(EmailPasswordInvalidException e) {
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.BAD_REQUEST.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(CreateUserUnauthorizedException.class)
    public ResponseEntity<ApiError> handleCreateUserUnauthorizedException(CreateUserUnauthorizedException e) {
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.UNAUTHORIZED.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(DeleteUserUnauthorizedException.class)
    public ResponseEntity<ApiError> handleDeleteUserUnauthorizedException(DeleteUserUnauthorizedException e) {
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.UNAUTHORIZED.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(UnauthorizedFileAccessException.class)
    public ResponseEntity<ApiError> handleUnauthorizedFileAccessException(UnauthorizedFileAccessException e){
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.UNAUTHORIZED.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFoundException(UserNotFoundException e) {
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.NOT_FOUND.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ApiError> handleTicketNotFoundException(TicketNotFoundException e){
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.NOT_FOUND.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(CustomerAndTicketNotMatchException.class)
    public ResponseEntity<ApiError> handleCustomerAndTicketNotMatchException(CustomerAndTicketNotMatchException e){
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.BAD_REQUEST.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(VerificationPhotoNotFoundException.class)
    public ResponseEntity<ApiError> handleVerificationPhotoNotFoundException(VerificationPhotoNotFoundException e){
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.NOT_FOUND.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ApiError> handleFileNotFoundException(FileNotFoundException e){
        ApiError apiError = new ApiError();
        apiError.setError("Le fichier que vous cherchez n'a pas été trouvé.");
        apiError.setCode(HttpStatus.NOT_FOUND.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(TicketAlreadyUsedException.class)
    public ResponseEntity<ApiError> handleTicketAlreadyUsedException(TicketAlreadyUsedException e){
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.BAD_REQUEST.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(CheckoutNotPayedException.class)
    public ResponseEntity<ApiError> handleCheckoutNotPayedException(CheckoutNotPayedException e){
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        ApiError apiError = new ApiError();
        apiError.setError(e.getMessage());
        apiError.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        apiError.setTimestamp(LocalDateTime.now());

        logger.error(e.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header(String.valueOf(headersCORS.headers()))
                .body(apiError);
    }
}

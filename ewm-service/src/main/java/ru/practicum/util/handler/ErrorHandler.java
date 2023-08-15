package ru.practicum.util.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.util.exception.EntityNotFoundException;
import ru.practicum.util.exception.event.EventConstraintException;
import ru.practicum.util.exception.request.ConfirmationNotRequiredException;
import ru.practicum.util.exception.request.RequestConstraintException;
import ru.practicum.util.exception.user.UserAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private final String incorrectParamsMsg = "Incorrect request params.";

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleModelFieldsConstraint(HttpServletRequest request,
                                                                     final MethodArgumentNotValidException e) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.append("Field: ")
                    .append(error.getField())
                    .append(": ")
                    .append("Error: ")
                    .append(error.getDefaultMessage())
                    .append(", ");
        }
        errors.deleteCharAt(errors.length() - 2);
        return buildErrorResponse(request,
                errors.toString(),
                incorrectParamsMsg,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpServletRequest request,
                                                          final Exception e) {
        return buildErrorResponse(request,
                e.getMessage(),
                incorrectParamsMsg,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            ConfirmationNotRequiredException.class,
            UserAccessException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpServletRequest request,
                                                          final RuntimeException e) {
        return buildErrorResponse(request, e.getMessage(), incorrectParamsMsg,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(HttpServletRequest request,
                                                        final RuntimeException e) {
        return buildErrorResponse(request,
                e.getMessage(),
                "Entity not found.",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            EventConstraintException.class,
            IllegalArgumentException.class,
            RequestConstraintException.class
    })
    public ResponseEntity<ErrorResponse> handleConflict(HttpServletRequest request,
                                                        final RuntimeException e) {
        return buildErrorResponse(request,
                e.getMessage(),
                "Integrity constraint violated.",
                HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleThrowable(HttpServletRequest request,
                                                         final Throwable e) {
        return buildErrorResponse(request,
                e.getMessage(),
                "Internal Server Error occurred during request processing.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpServletRequest request,
                                                             String message,
                                                             String reason,
                                                             HttpStatus httpStatus) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(httpStatus)
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        log.error("{} {} {}", request.getMethod(), request.getRequestURI(), errorResponse);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}

package io.joffrey.ccpp.projectplanning.infrastructure.rest.exception;

import com.ccpp.shared.exception.DomainException;
import io.joffrey.ccpp.projectplanning.application.query.exception.ProjectNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProjectNotFoundException.class)
    ErrorResponse handleProjectNotFoundException(ProjectNotFoundException ex) {
        return new ErrorResponse("PROJECT_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(DomainException.class)
    ErrorResponse handleDomainException(DomainException ex) {
        return new ErrorResponse("", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ErrorResponse("", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    ErrorResponse handleException(Exception ex) {
        return new ErrorResponse("", ex.getMessage());
    }


    record ErrorResponse(
            String code,
            String message
    ) {
    }

}

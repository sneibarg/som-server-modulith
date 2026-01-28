package org.springy.som.modulith.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.area.AreaNotFoundException;
import org.springy.som.modulith.exception.area.AreaPersistenceException;
import org.springy.som.modulith.exception.area.InvalidAreaException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AreaNotFoundException.class)
    public ResponseEntity<ProblemDetail> notFound(AreaNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Area not found");
        pd.setDetail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(pd);
    }

    @ExceptionHandler(InvalidAreaException.class)
    public ResponseEntity<ProblemDetail> handleInvalid(InvalidAreaException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid request");
        pd.setDetail(ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(pd);
    }

    @ExceptionHandler(AreaPersistenceException.class)
    ResponseEntity<?> unavailable(AreaPersistenceException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        pd.setTitle("Service unavailable");
        pd.setDetail(ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(pd);
    }
}


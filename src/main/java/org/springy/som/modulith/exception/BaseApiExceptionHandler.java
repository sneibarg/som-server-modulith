package org.springy.som.modulith.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class BaseApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handle(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Request body failed validation");

        // optional: include field errors
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        fe -> Objects.toString(fe.getDefaultMessage(), "invalid"),
                        (a, b) -> a,
                        LinkedHashMap::new));
        pd.setProperty("errors", errors);

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pd);
    }

    @ExceptionHandler(NotFoundApiException.class)
    public ResponseEntity<ProblemDetail> notFound(NotFoundApiException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Not found");
        pd.setDetail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(pd);
    }

    @ExceptionHandler(InvalidRequestApiException.class)
    public ResponseEntity<ProblemDetail> handleInvalid(InvalidRequestApiException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Invalid request");
        pd.setDetail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(pd);
    }

    @ExceptionHandler(ServiceUnavailableApiException.class)
    public ResponseEntity<ProblemDetail> unavailable(ServiceUnavailableApiException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        pd.setTitle("Service unavailable");
        pd.setDetail(ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(pd);
    }
}


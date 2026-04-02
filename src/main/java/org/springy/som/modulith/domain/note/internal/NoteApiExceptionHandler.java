package org.springy.som.modulith.domain.note.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = NoteController.class)
public class NoteApiExceptionHandler extends BaseApiExceptionHandler {
}

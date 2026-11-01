package org.springy.som.modulith.memory.internal;

import org.springy.som.modulith.exception.BaseApiExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = MemoryController.class)
public class MemoryApiExceptionHandler extends BaseApiExceptionHandler { }

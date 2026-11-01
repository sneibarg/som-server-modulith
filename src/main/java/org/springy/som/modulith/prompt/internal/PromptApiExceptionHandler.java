package org.springy.som.modulith.prompt.internal;

import org.springy.som.modulith.exception.BaseApiExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = PromptController.class)
public class PromptApiExceptionHandler extends BaseApiExceptionHandler { }

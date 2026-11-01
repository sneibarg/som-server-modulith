package org.springy.som.modulith.knowledge.internal;

import org.springy.som.modulith.exception.BaseApiExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = KnowledgeController.class)
public class KnowledgeApiExceptionHandler extends BaseApiExceptionHandler { }

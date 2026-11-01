package org.springy.som.modulith.agents.internal;

import org.springy.som.modulith.exception.BaseApiExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AgentController.class)
public class AgentApiExceptionHandler extends BaseApiExceptionHandler { }

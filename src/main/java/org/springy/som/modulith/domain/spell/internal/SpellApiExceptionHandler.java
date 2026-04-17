package org.springy.som.modulith.domain.spell.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = SpellController.class)
public class SpellApiExceptionHandler extends BaseApiExceptionHandler { }



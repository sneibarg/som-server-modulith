package org.springy.som.modulith.domain.character.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;


@RestControllerAdvice(assignableTypes = CharacterController.class)
public class PlayerCharacterApiExceptionHandler extends BaseApiExceptionHandler { }


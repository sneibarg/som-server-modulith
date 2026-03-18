package org.springy.som.modulith.domain.skill.internal;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = SkillController.class)
public class SkillApiExceptionHandler extends BaseApiExceptionHandler { }



package org.springy.som.modulith.exception.area;

import org.springy.som.modulith.domain.area.internal.AreaController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(assignableTypes = AreaController.class)
public class AreaApiExceptionHandler extends BaseApiExceptionHandler { }



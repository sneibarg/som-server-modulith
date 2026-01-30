package org.springy.som.modulith.exception.clazz;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springy.som.modulith.controller.ClassController;
import org.springy.som.modulith.exception.BaseApiExceptionHandler;

@RestControllerAdvice(assignableTypes = ClassController.class)
public class RomClassApiExceptionHandler extends BaseApiExceptionHandler {
}

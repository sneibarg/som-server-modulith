package org.springy.som.modulith.domain.skill.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "SkillDocument not found", code = "SKILL_NOT_FOUND")
public class SkillNotFoundException extends NotFoundApiException {
    public SkillNotFoundException(String message) {
        super(message);
    }
}


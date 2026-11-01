package org.springy.som.modulith.agents.internal;

import org.springframework.http.HttpStatus;
import org.springy.som.modulith.exception.ApiError;
import org.springy.som.modulith.exception.NotFoundApiException;

@ApiError(status = HttpStatus.NOT_FOUND, title = "Agent not found", code = "AGENT_NOT_FOUND")
public class AgentNotFoundException extends NotFoundApiException {
    public AgentNotFoundException(String message) {
        super(message);
    }
}

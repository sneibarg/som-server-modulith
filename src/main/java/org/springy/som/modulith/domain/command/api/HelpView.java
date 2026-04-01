package org.springy.som.modulith.domain.command.api;

public record HelpView(
    String id,
    int level,
    String keyword,
    String text
) {}

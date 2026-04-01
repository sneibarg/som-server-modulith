package org.springy.som.modulith.domain.command.api;

public record SocialView(
    String id,
    String name,
    String charNoArg,
    String othersNoArg,
    String charFound,
    String othersFound,
    String victFound,
    String charNotFound,
    String charAuto,
    String othersAuto
) {}

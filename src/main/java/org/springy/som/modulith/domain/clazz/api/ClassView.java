package org.springy.som.modulith.domain.clazz.api;

public record ClassView(
        String id,
        String name,
        String whoName,
        String primaryAttribute,
        String startingWeapon,
        String baseGroup,
        String defaultGroup,
        int skillAdept,
        int thac0_00,
        int thac0_32,
        int hpMin,
        int hpMax,
        boolean fMana
) {}

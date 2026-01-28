package org.springy.som.modulith.exception.area;

public class AreaNotFoundException extends RuntimeException {
    public AreaNotFoundException(String areaId) {
        super("Area not found: " + areaId);
    }
}

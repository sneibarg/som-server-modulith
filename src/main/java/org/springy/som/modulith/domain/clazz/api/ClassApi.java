package org.springy.som.modulith.domain.clazz.api;

import org.springy.som.modulith.domain.clazz.internal.ClassDocument;

import java.util.List;

public interface ClassApi {
    List<ClassDocument> getAllClasses();
    ClassDocument getRomClassById(String id);
    ClassDocument createRomClass(ClassDocument classDocument);
    ClassDocument saveRomClassForId(String id, ClassDocument classDocument);
    void deleteRomClassById(String id);
    long deleteAllRomClasses();
}

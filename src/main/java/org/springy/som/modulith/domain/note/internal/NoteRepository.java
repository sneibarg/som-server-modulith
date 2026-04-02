package org.springy.som.modulith.domain.note.internal;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface NoteRepository extends MongoRepository<NoteDocument, String> {
    @NotNull List<NoteDocument> findAll();

    @Query("{type: ?0}")
    List<NoteDocument> findNotesByType(int type);

    @Query("{id: '?0'}")
    NoteDocument findNoteById(String noteId);
}

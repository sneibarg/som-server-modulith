package org.springy.som.modulith.domain.note.api;

import org.springy.som.modulith.domain.note.internal.NoteDocument;

import java.util.List;

public interface NoteApi {
    List<NoteDocument> getAllNotes();
    List<NoteDocument> getNotesByType(int type);
    NoteDocument getNoteById(String noteId);
    NoteDocument createNote(NoteDocument noteDocument);
    NoteDocument saveNoteForId(String id, NoteDocument noteDocument);
    void deleteNoteById(String id);
    long deleteAllNotes();
}

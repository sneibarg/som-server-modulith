package org.springy.som.modulith.domain.note.api;

import org.springy.som.modulith.domain.note.internal.NoteDocument;

public final class NoteMapper {

    private NoteMapper() {}

    public static NoteView toView(NoteDocument doc) {
        return new NoteView(
            doc.getId(),
            doc.getType(),
            doc.getSender(),
            doc.getDate(),
            doc.getToList(),
            doc.getSubject(),
            doc.getText(),
            doc.getDateStamp()
        );
    }
}

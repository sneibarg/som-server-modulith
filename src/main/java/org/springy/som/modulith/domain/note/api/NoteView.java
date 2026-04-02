package org.springy.som.modulith.domain.note.api;

public record NoteView(
    String id,
    int type,
    String sender,
    String date,
    String toList,
    String subject,
    String text,
    long dateStamp
) {}

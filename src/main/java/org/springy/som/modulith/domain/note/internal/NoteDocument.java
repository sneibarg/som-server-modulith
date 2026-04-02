package org.springy.som.modulith.domain.note.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("Notes")
public class NoteDocument {
    private int type;
    @NotBlank(message = "sender must not be blank")
    private String sender;
    private String date;
    private String toList;
    @NotBlank(message = "subject must not be blank")
    private String subject;
    private String text;
    private long dateStamp;

    @Id
    private String id;
}

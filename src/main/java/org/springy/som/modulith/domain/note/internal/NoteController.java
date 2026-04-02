package org.springy.som.modulith.domain.note.internal;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springy.som.modulith.domain.note.api.NoteMapper;
import org.springy.som.modulith.domain.note.api.NoteView;
import org.springy.som.modulith.web.DeleteAllResponse;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/v1/notes", produces = "application/json")
public class NoteController {
    private final NoteService noteService;
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public ResponseEntity<List<NoteView>> getNotes() {
        List<NoteView> noteViews = noteService.getAllNotes()
                .stream()
                .map(NoteMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(noteViews);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<NoteView> getNoteById(@Valid @PathVariable String id) {
        return ResponseEntity.ok(NoteMapper.toView(noteService.getNoteById(id)));
    }

    @GetMapping(path = "/type/{type}")
    public ResponseEntity<List<NoteView>> getNotesByType(@PathVariable int type) {
        List<NoteView> noteViews = noteService.getNotesByType(type)
                .stream()
                .map(NoteMapper::toView)
                .collect(Collectors.toList());
        return ResponseEntity.ok(noteViews);
    }

    @PostMapping
    public ResponseEntity<NoteView> createNote(@Valid @RequestBody NoteDocument noteDocument) {
        NoteDocument saved = noteService.createNote(noteDocument);
        NoteView noteView = NoteMapper.toView(saved);
        return ResponseEntity
                .created(URI.create("/api/v1/notes/" + saved.getId()))
                .body(noteView);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteView> updateNote(@PathVariable String id, @Valid @RequestBody NoteDocument noteDocument) {
        NoteDocument updated = noteService.saveNoteForId(id, noteDocument);
        NoteView noteView = NoteMapper.toView(updated);
        return ResponseEntity.ok(noteView);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable String id) {
        noteService.deleteNoteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<DeleteAllResponse> deleteAll() {
        return ResponseEntity.ok(new DeleteAllResponse(noteService.deleteAllNotes()));
    }
}

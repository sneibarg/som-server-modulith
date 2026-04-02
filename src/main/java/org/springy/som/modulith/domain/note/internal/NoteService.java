package org.springy.som.modulith.domain.note.internal;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springy.som.modulith.domain.note.api.NoteApi;

import java.util.List;

import static org.springy.som.modulith.domain.DomainGuards.noteIdMissing;
import static org.springy.som.modulith.domain.ServiceGuards.requireEntityWithId;
import static org.springy.som.modulith.domain.ServiceGuards.requireText;
import static org.springy.som.modulith.domain.ServiceGuards.safeId;

@Slf4j
@Service
public class NoteService implements NoteApi {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @CircuitBreaker(name = "somAPI", fallbackMethod = "getAllNotesFallback")
    @Retry(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<NoteDocument> getAllNotes() {
        return noteRepository.findAll();
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public List<NoteDocument> getNotesByType(@RequestParam int type) {
        return noteRepository.findNotesByType(type);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public NoteDocument getNoteById(@RequestParam String noteId) {
        return noteRepository.findNoteById(noteId);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public NoteDocument createNote(@Valid @RequestBody NoteDocument noteDocument) {
        requireEntityWithId(noteDocument, NoteDocument::getId, noteIdMissing(), noteIdMissing());

        try {
            // if (noteRepository.existsById(noteDocument.getId())) throw new NoteConflictException(...)
            return noteRepository.save(noteDocument);
        } catch (DataAccessException ex) {
            log.warn("DB failure in createNote noteId={}", safeId(noteDocument, NoteDocument::getId), ex);
            throw new NotePersistenceException("Failed to create noteDocument" + ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public NoteDocument saveNoteForId(String id, NoteDocument noteDocument) {
        NoteDocument existing = noteRepository.findNoteById(id);
        requireText(existing.getId(), noteIdMissing());
        requireEntityWithId(existing, NoteDocument::getId, noteIdMissing(), noteIdMissing());

        return noteRepository.save(noteDocument);
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public void deleteNoteById(String id) {
        requireText(id, noteIdMissing());

        try {
            if (!noteRepository.existsById(id)) {
                throw new NoteNotFoundException(id);
            }
            noteRepository.deleteById(id);
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteNoteById id={}", id, ex);
            throw new NotePersistenceException("Failed to delete note: " + id + " " + ex);
        }
    }

    @CircuitBreaker(name = "somAPI")
    @Bulkhead(name = "somAPI")
    public long deleteAllNotes() {
        try {
            long itemCount = noteRepository.count();
            noteRepository.deleteAll();
            return itemCount;
        } catch (DataAccessException ex) {
            log.warn("DB failure in deleteAllNotes", ex);
            throw new NotePersistenceException("Failed to delete all notes " + ex);
        }
    }

    private List<NoteDocument> getAllNotesFallback(Throwable t) {
        log.warn("Fallback getAllNotes due to {}", t.toString());
        return List.of();
    }

    private NoteDocument getNoteByIdFallback(String id, Throwable t) {
        log.warn("Fallback getNoteById id={} due to {}", id, t.toString());
        throw new NotePersistenceException("NoteDocument lookup temporarily unavailable: " + id + " " + t);
    }
}

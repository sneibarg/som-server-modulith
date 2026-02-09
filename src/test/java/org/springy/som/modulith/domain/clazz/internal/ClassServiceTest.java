package org.springy.som.modulith.domain.clazz.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springy.som.modulith.domain.ServiceGuards;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassServiceTest {

    private ClassRepository repo;
    private ClassService service;

    @BeforeEach
    void setUp() {
        repo = mock(ClassRepository.class);
        service = new ClassService(repo);
    }

    @Test
    void getAllClasses_ok_returnsList() {
        List<ClassDocument> expected = List.of(new ClassDocument(), new ClassDocument());
        when(repo.findAll()).thenReturn(expected);

        List<ClassDocument> actual = service.getAllClasses();

        assertThat(actual).isSameAs(expected);
        verify(repo).findAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getRomClassById_ok_returnsEntityOrNull_passthrough() {
        ClassDocument rc = new ClassDocument();
        when(repo.findRomClassById("C1")).thenReturn(rc);

        ClassDocument actual = service.getRomClassById("C1");

        assertThat(actual).isSameAs(rc);
        verify(repo).findRomClassById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomClass_null_throwsInvalid() {
        assertThatThrownBy(() -> service.createRomClass(null))
                .isInstanceOf(InvalidClassException.class)
                .hasMessageContaining("must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void createRomClass_blankId_throwsInvalid() {
        ClassDocument rc = mock(ClassDocument.class);
        when(rc.getId()).thenReturn(" ");

        assertThatThrownBy(() -> service.createRomClass(rc))
                .isInstanceOf(InvalidClassException.class)
                .hasMessageContaining("id must be provided");

        verify(rc).getId();
        verifyNoInteractions(repo);
    }

    @Test
    void createRomClass_ok_saves() {
        ClassDocument rc = mock(ClassDocument.class);
        when(rc.getId()).thenReturn("C1");
        when(repo.save(rc)).thenReturn(rc);

        ClassDocument actual = service.createRomClass(rc);

        assertThat(actual).isSameAs(rc);

        verify(rc).getId();
        verify(repo).save(rc);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void createRomClass_dataAccess_becomesPersistenceException() {
        ClassDocument rc = mock(ClassDocument.class);
        when(rc.getId()).thenReturn("C1");
        when(repo.save(rc)).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.createRomClass(rc))
                .isInstanceOf(ClassPersistenceException.class)
                .hasMessageContaining("Failed to create area");

        verify(repo).save(rc);
        verifyNoMoreInteractions(repo);
    }

    @Test
    void safeId_whenGetIdThrows_returnsNull_reflection() {
        ClassDocument badClass = mock(ClassDocument.class);
        when(badClass.getId()).thenThrow(new RuntimeException("boom"));

        String result = ServiceGuards.safeId(badClass, ClassDocument::getId);

        assertThat(result).isNull();
    }

    @Test
    void saveRomClassForId_blankId_throwsInvalid() {
        ClassDocument rc = mock(ClassDocument.class);
        when(rc.getId()).thenReturn("C1");

        assertThatThrownBy(() -> service.saveRomClassForId(" ", rc))
                .isInstanceOf(InvalidClassException.class)
                .hasMessageContaining("id must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveRomClassForId_nullBody_throwsInvalid() {
        assertThatThrownBy(() -> service.saveRomClassForId("C1", null))
                .isInstanceOf(InvalidClassException.class)
                .hasMessageContaining("must be provided");

        verifyNoInteractions(repo);
    }

    @Test
    void saveRomClassForId_blankBodyId_throwsInvalid() {
        ClassDocument rc = mock(ClassDocument.class);
        when(rc.getId()).thenReturn("");

        assertThatThrownBy(() -> service.saveRomClassForId("C1", rc))
                .isInstanceOf(InvalidClassException.class)
                .hasMessageContaining("id must be provided");

        verify(rc).getId();
        verifyNoInteractions(repo);
    }

    @Test
    void saveRomClassForId_ok_loadsThenSavesLoadedEntity() {
        ClassDocument input = mock(ClassDocument.class);
        when(input.getId()).thenReturn("C1");

        ClassDocument loaded = new ClassDocument();
        when(repo.findRomClassById("C1")).thenReturn(loaded);
        when(repo.save(loaded)).thenReturn(loaded);

        ClassDocument actual = service.saveRomClassForId("C1", input);

        assertThat(actual).isSameAs(loaded);

        verify(input).getId();                 // requireRomClass(input)
        verify(repo).findRomClassById("C1");   // getRomClassById
        verify(repo).save(loaded);             // save loaded entity (current impl)
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomClassById_blankId_throwsInvalid() {
        assertThatThrownBy(() -> service.deleteRomClassById(""))
                .isInstanceOf(InvalidClassException.class);

        verifyNoInteractions(repo);
    }

    @Test
    void deleteRomClassById_notFound_throwsNotFound() {
        when(repo.existsById("C1")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteRomClassById("C1"))
                .isInstanceOf(ClassNotFoundException.class)
                .hasMessageContaining("C1");

        verify(repo).existsById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomClassById_ok_deletes() {
        when(repo.existsById("C1")).thenReturn(true);

        service.deleteRomClassById("C1");

        verify(repo).existsById("C1");
        verify(repo).deleteById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteRomClassById_dataAccess_becomesPersistenceException() {
        when(repo.existsById("C1")).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteRomClassById("C1"))
                .isInstanceOf(ClassPersistenceException.class)
                .hasMessageContaining("Failed to delete area");

        verify(repo).existsById("C1");
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRomClasses_ok_returnsCountAndDeletes() {
        when(repo.count()).thenReturn(7L);

        long deleted = service.deleteAllRomClasses();

        assertThat(deleted).isEqualTo(7L);

        verify(repo).count();
        verify(repo).deleteAll();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void deleteAllRomClasses_dataAccess_becomesPersistenceException() {
        when(repo.count()).thenThrow(new DataAccessResourceFailureException("db down"));

        assertThatThrownBy(() -> service.deleteAllRomClasses())
                .isInstanceOf(ClassPersistenceException.class)
                .hasMessageContaining("Failed to delete all areas");

        verify(repo).count();
        verifyNoMoreInteractions(repo);
    }

    @Test
    void getAllRomClassesFallback_returnsEmptyList_reflection() throws Exception {
        Method m = ClassService.class.getDeclaredMethod("getAllRomClassesFallback", Throwable.class);
        m.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ClassDocument> result = (List<ClassDocument>) m.invoke(service, new RuntimeException("cb"));

        assertThat(result).isEmpty();
    }

    @Test
    void getRomClassByIdFallback_throwsPersistenceException_reflection() throws Exception {
        Method m = ClassService.class.getDeclaredMethod("getRomClassByIdFallback", String.class, Throwable.class);
        m.setAccessible(true);

        assertThatThrownBy(() -> {
            try {
                m.invoke(service, "C1", new RuntimeException("cb"));
            } catch (Exception e) {
                throw e.getCause(); // unwrap InvocationTargetException
            }
        }).isInstanceOf(ClassPersistenceException.class)
                .hasMessageContaining("temporarily unavailable")
                .hasMessageContaining("C1");
    }
}

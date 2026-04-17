package org.springy.som.modulith.domain.spell.api;

import org.springy.som.modulith.domain.spell.internal.SpellDocument;

import java.util.List;

public interface SpellApi {
    List<SpellDocument> getAllSpells();
    SpellDocument getSpellById(String id);
    SpellDocument getSpellByName(String name);
    SpellDocument createSpell(SpellDocument SpellDocument);
    SpellDocument saveSpellForId(String id, SpellDocument spellDocument);
    void deleteSpellById(String id);
    long deleteAllSpells();
}

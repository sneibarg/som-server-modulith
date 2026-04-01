package org.springy.som.modulith.domain.command.api;

import org.springy.som.modulith.domain.command.internal.HelpDocument;

import java.util.List;

public interface HelpApi {
    List<HelpDocument> getAllHelps();
    HelpDocument getHelpByKeyword(String keyword);
    HelpDocument getHelpById(String helpId);
    HelpDocument createHelp(HelpDocument helpDocument);
    HelpDocument saveHelpForId(String id, HelpDocument helpDocument);
    void deleteHelpById(String id);
    long deleteAllHelps();
}

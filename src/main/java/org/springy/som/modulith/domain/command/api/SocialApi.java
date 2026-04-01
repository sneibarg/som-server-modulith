package org.springy.som.modulith.domain.command.api;

import org.springy.som.modulith.domain.command.internal.SocialDocument;

import java.util.List;

public interface SocialApi {
    List<SocialDocument> getAllSocials();
    SocialDocument getSocialByName(String socialName);
    SocialDocument getSocialById(String socialId);
    SocialDocument createSocial(SocialDocument socialDocument);
    SocialDocument saveSocialForId(String id, SocialDocument socialDocument);
    void deleteSocialById(String id);
    long deleteAllSocials();
}

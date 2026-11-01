package org.springy.som.modulith.knowledge.internal;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface KnowledgeRepository extends MongoRepository<KnowledgeDocument, String> {
}

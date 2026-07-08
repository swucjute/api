package com.swucjute.api.domain.worship.document.repository;

import com.swucjute.api.domain.worship.document.WorshipTranscriptDocument;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WorshipTranscriptDocumentRepository
    extends MongoRepository<WorshipTranscriptDocument, String> {

  Optional<WorshipTranscriptDocument> findFirstByWorshipIdOrderByCreatedAtDesc(Long worshipId);
}

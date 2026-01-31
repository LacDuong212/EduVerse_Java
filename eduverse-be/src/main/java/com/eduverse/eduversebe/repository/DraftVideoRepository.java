package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.DraftVideo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DraftVideoRepository extends MongoRepository<DraftVideo,String> {
    List<DraftVideo> findTop500ByExpireAtBefore(Instant expiry);
}

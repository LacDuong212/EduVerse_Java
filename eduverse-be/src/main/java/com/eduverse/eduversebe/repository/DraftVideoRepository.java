package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.DraftVideo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DraftVideoRepository extends MongoRepository<DraftVideo,String> {
}

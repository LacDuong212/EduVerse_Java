package com.eduverse.eduversebe.repository;

import com.eduverse.eduversebe.model.Instructor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstructorRepository extends MongoRepository<Instructor, String> {

    Optional<Instructor> findByUser(String userId);
}

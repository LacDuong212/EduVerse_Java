package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.model.Student;
import com.eduverse.eduversebe.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final MongoTemplate mongoTemplate;

    public boolean existsByUserId(String userId) {
        return studentRepository.existsByUserId(userId);
    }

    public void createNewStudent(String userId) {
        Student student = new Student();
        student.setUserId(userId);
        studentRepository.save(student);
    }

    public void addCourses(String userId, List<String> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) throw new AppException(ErrorCodes.ADD_COURSES_FAILED);

        Query query = Query.query(Criteria.where("userId").is(userId));

        Update update = new Update()
                .addToSet("myCourses")
                .each(courseIds.stream()
                                .map(Student.MyCourse::new)
                                .toList())
                .inc("stats.totalCourses", courseIds.size())
                .set("updatedAt", Instant.now());

        mongoTemplate.updateFirst(query, update, Student.class);
    }
}

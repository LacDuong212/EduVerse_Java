package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.response.instructor.StudentsListItem;
import com.eduverse.eduversebe.model.Student;
import com.eduverse.eduversebe.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
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

    public List<StudentsListItem> getStudentsListForInstructor(List<ObjectId> studentIds,
                                                               List<ObjectId> courseIds,
                                                               String keyword,
                                                               String sortKey) {
        Sort.Direction direction =
                "nameDesc".equals(sortKey)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("user").in(studentIds)),

                Aggregation.lookup("users", "user", "_id", "user"),
                Aggregation.unwind("user"),

                Aggregation.addFields()
                        .addField("coursesEnrolled")
                        .withValue(
                                ArrayOperators.Size.lengthOfArray(
                                        ArrayOperators.Filter.filter("myCourses")
                                                .as("c")
                                                .by(ArrayOperators.In
                                                        .arrayOf(courseIds)
                                                        .containsValue("$$c.course")
                                                )
                                )
                        ).build(),

                Aggregation.sort(direction, "user.name"),

                Aggregation.project()
                        .and("_id").as("id")
                        .and("user.name").as("name")
                        .and("user.email").as("email")
                        .and("user.pfpImg").as("pfpImg")
                        .and("user.isActivated").as("isActive")
                        .and("coursesEnrolled").as("coursesEnrolled")
        );

        List<StudentsListItem> students = mongoTemplate
                .aggregate(aggregation, "students", StudentsListItem.class)
                .getMappedResults();

        if (keyword != null && !keyword.trim().isEmpty())
            students = fuzzySearch(students, keyword);

        return students;
    }

    private List<StudentsListItem> fuzzySearch(List<StudentsListItem> students, String keyword) {
        return students.stream().filter(student -> {
            int nameScore = FuzzySearch.weightedRatio(keyword, student.getName());
            int emailScore = FuzzySearch.weightedRatio(keyword, student.getEmail());
            return nameScore > 50 || emailScore > 50;
        }).sorted((s1, s2) -> {
            int score1 = FuzzySearch.weightedRatio(keyword, s1.getName());
            int score2 = FuzzySearch.weightedRatio(keyword, s2.getName());
            return score2 - score1;
        }).toList();
    }
}

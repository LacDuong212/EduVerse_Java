package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.response.PageResponse;
import com.eduverse.eduversebe.dto.response.instructor.MyStudentsStats;
import com.eduverse.eduversebe.dto.response.instructor.StudentsListItem;
import com.eduverse.eduversebe.model.Instructor;
import com.eduverse.eduversebe.repository.InstructorRepository;
import com.eduverse.eduversebe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final InstructorRepository instructorRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    private final StudentService studentService;

    public boolean existsByUserId(String instructorId) {
        return instructorRepository.existsByUserId(instructorId);
    }

    public List<String> getInstructorCourseIds(String instructorId) {
        Instructor instructor = instructorRepository.findByUserId(instructorId)
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyCourses())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyCourse::getCourseId)
                .toList();
    }

    public List<String> getInstructorStudentIds(String instructorId) {
        Instructor instructor = instructorRepository.findByUserId((instructorId))
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyStudents())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyStudent::getStudentId)
                .toList();
    }

    public void updateInstructorsStudentListFromCourseIds(String studentId, List<String> courseIds) {
        Query query = new Query();
        query.addCriteria(Criteria.where("myCourses.courseId").in(courseIds));
        query.addCriteria(Criteria.where("myStudents.studentId").ne(new ObjectId(studentId)));

        Update update = new Update()
                .addToSet("myStudents",
                        new Document("student", new ObjectId(studentId))
                                .append("addedAt", Instant.now())
                )
                .inc("stats.totalStudents", 1)
                .set("updatedAt", Instant.now());

        mongoTemplate.updateMulti(query, update, Instructor.class);
    }

    public PageResponse<StudentsListItem> getInstructorStudentsListMatchCriteria(
            String instructorId,
            int page,
            int limit,
            String searchKey,
            String sortKey
    ) {
        List<ObjectId> studentIds = this.getInstructorStudentIds(instructorId).stream()
                .map(ObjectId::new).toList();
        List<ObjectId> courseIds = this.getInstructorCourseIds(instructorId).stream()
                .map(ObjectId::new).toList();

        List<StudentsListItem> results = studentService.getStudentsListForInstructor(
                studentIds,
                courseIds,
                searchKey,
                sortKey
        );

        int total = results.size();
        int pageNum = Math.max(page, 1);
        int pageSize = Math.max(limit, 10);
        int fromIndex = (pageNum - 1) * pageSize;
        List<StudentsListItem> paginatedList;

        if (fromIndex >= total) {
            paginatedList = new ArrayList<>();
        } else {
            int toIndex = Math.min(fromIndex + pageSize, total);
            paginatedList = results.subList(fromIndex, toIndex);
        }

        return PageResponse.<StudentsListItem>builder()
                .data(paginatedList)
                .pagination(PageResponse.Pagination.builder()
                        .total(total)
                        .page(pageNum)
                        .totalPages((int) Math.ceil((double) total / pageSize))
                        .build())
                .build();
    }

    public MyStudentsStats getInstructorStudentsStats(String instructorId) {
        List<String> studentIds = getInstructorStudentIds(instructorId);

        if (studentIds.isEmpty()) {
            return MyStudentsStats.builder()
                    .totalStudents(0)
                    .totalActive(0)
                    .totalInactive(0)
                    .build();
        }

        int studentCount = studentIds.size();
        int activatedCount = Optional.ofNullable(userRepository.countByIdInAndIsActivated(studentIds, true))
                .orElse(0);

        return MyStudentsStats.builder()
                .totalStudents(studentCount)
                .totalActive(activatedCount)
                .totalInactive(studentCount - activatedCount)
                .build();
    }
}

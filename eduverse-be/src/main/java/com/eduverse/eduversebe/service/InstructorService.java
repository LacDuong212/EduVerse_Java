package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.CourseStatus;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.request.UpdateCoursePrivacyRequest;
import com.eduverse.eduversebe.dto.response.*;
import com.eduverse.eduversebe.dto.response.instructor.*;
import com.eduverse.eduversebe.mapper.CourseMapper;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.model.Instructor;
import com.eduverse.eduversebe.repository.CourseRepository;
import com.eduverse.eduversebe.repository.InstructorRepository;
import com.eduverse.eduversebe.repository.OrderRepository;
import com.eduverse.eduversebe.repository.UserRepository;
import com.eduverse.eduversebe.repository.projection.MonthlyEarningProjection;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InstructorService {

    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    private final CourseService courseService;
    private final ReviewService reviewService;
    private final StudentService studentService;

    private final CourseMapper courseMapper;

    private List<MonthlyDataItemResponse> fillMissingMonthsHelper(
            List<MonthlyEarningProjection> raw,
            Instant start,
            Instant end
    ) {
        Map<YearMonth, Double> dataMap = raw.stream()
                .collect(Collectors.toMap(
                        p -> YearMonth.of(p.getYear(), p.getMonth()),
                        MonthlyEarningProjection::getTotalEarning
                ));

        YearMonth startMonth = YearMonth.from(
                start.atZone(ZoneOffset.UTC)
        );
        YearMonth endMonth = YearMonth.from(
                end.atZone(ZoneOffset.UTC)
        );

        List<MonthlyDataItemResponse> result = new ArrayList<>();
        YearMonth cursor = startMonth;

        while (!cursor.isAfter(endMonth)) {
            result.add(MonthlyDataItemResponse.builder()
                    .period(cursor)
                    .value(dataMap.getOrDefault(cursor, 0.0))
                    .build());
            cursor = cursor.plusMonths(1);
        }

        return result;
    }

    public boolean existsByUserId(String userId) {
        return instructorRepository.existsByUserId(userId);
    }

    public boolean checkCourseOwnership(String userId, String courseId) {
        return courseService.checkCourseOwnership(courseId, userId);
    }

    public List<String> getInstructorCourseIds(String userId) {
        Instructor instructor = instructorRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyCourses())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyCourse::getCourseId)
                .toList();
    }

    public List<String> getInstructorStudentIds(String userId) {
        Instructor instructor = instructorRepository.findByUserId((userId))
                .orElseThrow(() -> new AppException(ErrorCodes.INSTRUCTOR_NOT_FOUND));
        return Optional.ofNullable(instructor.getMyStudents())
                .orElse(List.of())
                .stream()
                .map(Instructor.MyStudent::getStudentId)
                .toList();
    }

    public InstructorStats getInstructorStats(String userId) {
        List<String> courseIds = this.getInstructorCourseIds(userId);
        if (courseIds.isEmpty()) return InstructorStats.builder().build();

        return InstructorStats.builder()
                .totalCourses(courseIds.size())
                .totalStudents(this.getInstructorStudentIds(userId).size())
                .totalOrders(this.countCompletedOrdersByCourseIds(courseIds))
                .totalReviews(reviewService.getTotalReviewsOfCourses(courseIds))
                .averageRating(reviewService.getInstructorAvgRating(courseIds))
                .build();
    }

    public long countCompletedOrdersByCourseIds(List<String> courseIds) {
        if (courseIds == null) courseIds = List.of();
        return Optional.ofNullable(orderRepository.countCompletedOrdersByCourseIds(courseIds)).orElse(0L);
    }

    public List<MonthlyDataItemResponse> getCoursesMonthlyEarningPast12Months(String userId) {
        Instant start = YearMonth
                .now()
                .minusMonths(11)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        Instant end = Instant.now();

        return fillMissingMonthsHelper(
                orderRepository.getCoursesMonthlyEarningFromRange(this.getInstructorCourseIds(userId), start, end),
                start,
                end
        );
    }

    public List<CourseEarningDataResponse> getTop5EarningCoursesThisMonth(String userId) {
        List<ObjectId> courseIds = this.getInstructorCourseIds(userId).stream()
                .map(ObjectId::new)
                .toList();
        int limit = Math.min(5, courseIds.size());

        YearMonth now = YearMonth.now(ZoneOffset.UTC);
        Instant startOfMonth = now
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();
        Instant startOfNextMonth = now
                .plusMonths(1)
                .atDay(1)
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        return orderRepository.getTopEarningCoursesThisMonth(courseIds, startOfMonth, startOfNextMonth, limit)
                .stream()
                .map(c -> CourseEarningDataResponse.builder()
                        .id(c.getCourseId())
                        .title(c.getTitle())
                        .totalEarning(c.getTotalEarning())
                        .totalSales(c.getTotalSales())
                        .build())
                .toList();
    }

    public PageResponse<CoursesListItem> getInstructorCoursesListMatchCriteria(
            String userId,
            int page,
            int limit,
            String searchKey,
            String sortKey
    ) {
        return courseService.getCoursesMatchCriteriaForInstructor(
                userId,
                searchKey,
                sortKey,
                Math.max(page, 1),
                Math.max(limit, 5)
        );
    }

    public boolean changeCoursePrivacy(String courseId, UpdateCoursePrivacyRequest request) {
        return courseService.updateCoursePrivacy(courseId, request.privacy());
    }

    public MyCoursesStats getInstructorCoursesStats(String userId) {
        return MyCoursesStats.builder()
                .totalCourses(courseService.countInstructorCourses(userId))
                .totalLive(courseService.countCoursesWithStatusForInstructor(userId, CourseStatus.Live))
                .totalPending(courseService.countCoursesWithStatusForInstructor(userId, CourseStatus.Pending))
                .totalRejected(courseService.countCoursesWithStatusForInstructor(userId, CourseStatus.Rejected))
                .totalBlocked(courseService.countCoursesWithStatusForInstructor(userId, CourseStatus.Blocked))
                .build();
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
            String userId,
            int page,
            int limit,
            String searchKey,
            String sortKey
    ) {
        List<ObjectId> studentIds = this.getInstructorStudentIds(userId).stream()
                .map(ObjectId::new).toList();
        List<ObjectId> courseIds = this.getInstructorCourseIds(userId).stream()
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

    public MyStudentsStats getInstructorStudentsStats(String userId) {
        List<String> studentIds = getInstructorStudentIds(userId);

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

    public CourseData getInstructorCourseData(String userId, String courseId) {
        Course course = courseRepository.findByIdAndInstructor_RefAndIsDeletedFalse(courseId, userId);
        if (course == null) throw new AppException(ErrorCodes.COURSE_NOT_FOUND);
        else return courseMapper.toCourseData(course);
    }
}

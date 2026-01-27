package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.CourseStatus;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.request.CourseFilterRequest;
import com.eduverse.eduversebe.dto.response.*;
import com.eduverse.eduversebe.mapper.CourseMapper;
import com.eduverse.eduversebe.model.Category;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.repository.CategoryRepository;
import com.eduverse.eduversebe.repository.CourseRepository;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper courseMapper;
    private final MongoTemplate mongoTemplate;

    public CourseStatsResponse getCourseStats() {
        Criteria criteria = Criteria.where("isPrivate").is(false)
                .and("isDeleted").is(false)
                .and("status").is("Live");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group()
                        .count().as("count")
                        .sum("studentsEnrolled").as("totalLearners")
                        .sum("duration").as("totalDurationSeconds")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "courses", org.bson.Document.class);
        org.bson.Document statsDoc = results.getUniqueMappedResult();

        long totalCourses = 0;
        long totalLearners = 0;
        double totalDurationSeconds = 0;

        if (statsDoc != null) {
            Number countNum = statsDoc.get("count", Number.class);
            totalCourses = (countNum != null) ? countNum.longValue() : 0;

            Number learnersNum = statsDoc.get("totalLearners", Number.class);
            totalLearners = (learnersNum != null) ? learnersNum.longValue() : 0;

            Number durationNum = statsDoc.get("totalDurationSeconds", Number.class);
            totalDurationSeconds = (durationNum != null) ? durationNum.doubleValue() : 0.0;
        }

        Query query = new Query(criteria);
        List<Object> distinctInstructors = mongoTemplate.findDistinct(query, "instructor.ref", Course.class, Object.class);
        long totalInstructors = distinctInstructors.size();

        double totalHours = totalDurationSeconds / 3600.0;

        return CourseStatsResponse.builder()
                .totalCourses(totalCourses)
                .totalLearners(totalLearners)
                .totalHours(totalHours)
                .totalInstructors(totalInstructors)
                .build();
    }

    public HomeContentResponse getHomeCourses() {

        List<Course> newest = courseRepository.findByStatusAndIsPrivateFalseAndIsDeletedFalse(
                CourseStatus.Live,
                PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<Course> bestSellers = courseRepository.findByStatusAndIsPrivateFalseAndIsDeletedFalse(
                CourseStatus.Live,
                PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "studentsEnrolled"))
        );

        List<Course> topRated = courseRepository.findTopRatedCourses(
                CourseStatus.Live,
                PageRequest.of(0, 8, Sort.by(Sort.Order.desc("rating.average"), Sort.Order.desc("rating.count")))
        );

        List<Course> biggestDiscounts = courseRepository.findBiggestDiscounts(
                CourseStatus.Live, PageRequest.of(0, 8)
        );

        List<Course> allCourses = new ArrayList<>();
        allCourses.addAll(newest);
        allCourses.addAll(bestSellers);
        allCourses.addAll(topRated);
        allCourses.addAll(biggestDiscounts);

        Set<String> categoryIds = allCourses.stream()
                .map(Course::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Category> categories = categoryRepository.findAllById(categoryIds);

        Map<String, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        return HomeContentResponse.builder()
                .newest(mapToResponseList(newest, categoryMap))
                .bestSellers(mapToResponseList(bestSellers, categoryMap))
                .topRated(mapToResponseList(topRated, categoryMap))
                .biggestDiscounts(mapToResponseList(biggestDiscounts, categoryMap))
                .build();
    }

    private List<CourseResponse> mapToResponseList(List<Course> courses, Map<String, Category> categoryMap) {
        return courses.stream().map(course -> {
            CourseResponse res = courseMapper.toCourseResponse(course);

            if (course.getCategoryId() != null && categoryMap.containsKey(course.getCategoryId())) {
                Category cat = categoryMap.get(course.getCategoryId());

                CourseResponse.CategoryInfo catInfo = CourseResponse.CategoryInfo.builder()
                        .id(cat.getId())
                        .name(cat.getName())
                        .build();

                res.setCategory(catInfo);
            }
            return res;
        }).collect(Collectors.toList());
    }

    public PageResponse<CourseResponse> getAllCourses(CourseFilterRequest request) {

        Query query = new Query();
        query.addCriteria(Criteria.where("isPrivate").is(false));
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.addCriteria(Criteria.where("status").is(CourseStatus.Live));

        if (hasText(request.getCategory())) {
            query.addCriteria(Criteria.where("category").is(new ObjectId(request.getCategory())));
        }
        if (hasText(request.getSubCategory())) {
            query.addCriteria(Criteria.where("subCategory").is(request.getSubCategory()));
        }
        if (hasText(request.getLanguage())) {
            query.addCriteria(Criteria.where("language").is(request.getLanguage()));
        }
        if (hasText(request.getLevel()) && !"All".equalsIgnoreCase(request.getLevel())) {
            query.addCriteria(Criteria.where("level").is(request.getLevel()));
        }

        if ("free".equalsIgnoreCase(request.getPrice())) {
            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("price").is(0),
                    Criteria.where("discountPrice").is(0)
            ));
        } else if ("paid".equalsIgnoreCase(request.getPrice())) {
            query.addCriteria(Criteria.where("price").gt(0));
        }

        List<Course> allCourses = mongoTemplate.find(query, Course.class);

        if (hasText(request.getSearch())) {
            String keyword = request.getSearch().trim();
            allCourses = this.fuzzySearch(allCourses, keyword);
        } else {
            applySorting(allCourses, request.getSort());
        }

        int total = allCourses.size();
        int page = Math.max(request.getPage(), 1);
        int limit = Math.max(request.getLimit(), 1);

        List<CourseResponse> content = getPaginatedList(page, limit, allCourses, total).stream()
                .map(this::mapToCourseResponseWithCalculations)
                .collect(Collectors.toList());

        return PageResponse.<CourseResponse>builder()
                .data(content)
                .pagination(PageResponse.Pagination.builder()
                        .total(total)
                        .page(page)
                        .totalPages((int) Math.ceil((double) total / limit))
                        .build())
                .build();
    }

    private List<Course> fuzzySearch(List<Course> courses, String keyword) {
        return courses.stream().filter(course -> {
                    int scoreTitle = FuzzySearch.weightedRatio(keyword, course.getTitle());
                    int scoreSub = FuzzySearch.weightedRatio(keyword, course.getSubtitle());

                    return scoreTitle > 50 || scoreSub > 50;
                })
                .sorted((c1, c2) -> {
                    int score1 = FuzzySearch.weightedRatio(keyword, c1.getTitle());
                    int score2 = FuzzySearch.weightedRatio(keyword, c2.getTitle());
                    return score2 - score1;
                })
                .collect(Collectors.toList());
    }

    private void applySorting(List<Course> courses, String sortParam) {
        if (sortParam == null) sortParam = "newest";

        Function<Course, Double> getEffectivePrice = c -> {
            if (c.getDiscountPrice() != null && c.getDiscountPrice() > 0) {
                return c.getDiscountPrice();
            }
            return c.getPrice();
        };

        switch (sortParam) {
            case "oldest":
                courses.sort(Comparator.comparing(Course::getCreatedAt));
                break;
            case "priceHighToLow":
                courses.sort(Comparator.comparing(getEffectivePrice, Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case "priceLowToHigh":
                courses.sort(Comparator.comparing(getEffectivePrice, Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "mostPopular":
                courses.sort(Comparator.comparing(
                        Course::getStudentsEnrolled,
                        Comparator.nullsLast(Comparator.reverseOrder()))
                );
                break;
            case "leastPopular":
                courses.sort(Comparator.comparing(
                        Course::getStudentsEnrolled,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                );
                break;
            case "highestRating":
                courses.sort(Comparator.comparing(
                        c -> c.getRating().getAverage(),
                        Comparator.nullsLast(Comparator.reverseOrder()))
                );
                break;
            case "lowestRating":
                courses.sort(Comparator.comparing(
                        c -> c.getRating().getAverage(),
                        Comparator.nullsLast(Comparator.naturalOrder()))
                );
                break;
            case "newest":
            default:
                courses.sort(Comparator.comparing(Course::getCreatedAt).reversed());
        }
    }

    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    private CourseResponse mapToCourseResponseWithCalculations(Course c) {
        CourseResponse res = courseMapper.toCourseResponse(c);

        if (c.getDuration() == null || c.getDuration() == 0) {
            double calculatedDuration = c.getCurriculum().stream()
                    .flatMap(sec -> sec.getLectures().stream())
                    .mapToDouble(lec -> lec.getDuration() != null ? lec.getDuration() : 0)
                    .sum();
            res.setDuration(calculatedDuration);
        }

        if (c.getLecturesCount() == null || c.getLecturesCount() == 0) {
            int calculatedLectures = c.getCurriculum().stream()
                    .mapToInt(sec -> sec.getLectures().size())
                    .sum();
            res.setLecturesCount(calculatedLectures);
        }

        return res;
    }

    public CourseFilterResponse getCourseFilters() {

        CompletableFuture<List<CourseFilterResponse.CategoryDto>> categoriesFuture = CompletableFuture.supplyAsync(() -> {
            List<Category> categories = categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

            return categories.stream()
                    .map(cat -> CourseFilterResponse.CategoryDto.builder()
                            .id(cat.getId())
                            .name(cat.getName())
                            .slug(cat.getSlug())
                            .build())
                    .collect(Collectors.toList());
        });

        CompletableFuture<List<String>> languagesFuture = CompletableFuture.supplyAsync(() -> {
            Query query = new Query();
            query.addCriteria(Criteria.where("isPrivate").is(false));
            query.addCriteria(Criteria.where("isDeleted").is(false));
            query.addCriteria(Criteria.where("status").is(CourseStatus.Live));

            List<String> distinctLangs = mongoTemplate.findDistinct(query, "language", Course.class, String.class);

            return distinctLangs.stream()
                    .filter(lang -> lang != null && !lang.trim().isEmpty())
                    .collect(Collectors.toList());
        });

        List<String> levels = Arrays.asList("All", "Beginner", "Intermediate", "Advanced");

        return CourseFilterResponse.builder()
                .categories(categoriesFuture.join())
                .languages(languagesFuture.join())
                .levels(levels)
                .build();
    }

    public PageResponse<InstructorCoursesListItemResponse> getCoursesMatchCriteriaForInstructor(String instructorId,
                                                                                        String searchKey,
                                                                                        String sortKey,
                                                                                        int pageNum,
                                                                                        int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.addCriteria(Criteria.where("instructor.ref").in(instructorId));
        query.limit(100);   // yes.

        List<Course> results = mongoTemplate.find(query, Course.class);
        if (hasText(searchKey)) {
            results = this.fuzzySearch(results, searchKey);
        } else if (hasText(sortKey)) {
            this.applySorting(results, sortKey);
        } else {
            query.with(Sort.by(Sort.Direction.DESC, "updatedAt"));
        }

        int total = results.size();

        List<InstructorCoursesListItemResponse> mappedList = getPaginatedList(pageNum, pageSize, results, total)
                .stream()
                .map(courseMapper::toInstructorCoursesListItemResponse)
                .toList();

        return PageResponse.<InstructorCoursesListItemResponse>builder()
                .data(mappedList)
                .pagination(PageResponse.Pagination.builder()
                        .total(total)
                        .page(pageNum)
                        .totalPages((int) Math.ceil((double) total / pageSize))
                        .build())
                .build();
    }

    private List<Course> getPaginatedList(int pageNum, int pageSize, List<Course> results, int total) {
        int fromIndex = (pageNum - 1) * pageSize;

        if (fromIndex >= total) {
            return Collections.emptyList();
        } else {
            int toIndex = Math.min(fromIndex + pageSize, total);
            return results.subList(fromIndex, toIndex);
        }
    }

    public boolean updateCoursePrivacy(String id, boolean privacy) {
        if (!courseRepository.existsById(id)) throw new AppException(ErrorCodes.COURSE_NOT_FOUND);

        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));

        Update update = new Update();
        update.set("isPrivate", privacy);
        update.set("updatedAt", Instant.now());

        UpdateResult result = mongoTemplate.updateFirst(query, update, Course.class);

        return result.getMatchedCount() > 0;
    }

    public int countInstructorCourses(String instructorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.addCriteria(Criteria.where("instructor.ref").is(instructorId));

        return Math.toIntExact(mongoTemplate.count(query, Course.class));
    }

    public int countCoursesWithStatusForInstructor(String instructorId, @DefaultValue("") CourseStatus status) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").is(false));
        query.addCriteria(Criteria.where("instructor.ref").is(instructorId));
        query.addCriteria(Criteria.where("status").is(status));

        return Math.toIntExact(mongoTemplate.count(query, Course.class));
    }

    public boolean checkCourseOwnership(String courseId, String instructorId) {
        return courseRepository.existsByIdAndInstructor_Ref(courseId, instructorId);
    }

    public void bulkUpdateCoursesEnrollmentCount(List<String> courseIds) {
        BulkOperations bulkOps =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Course.class);
        courseIds.forEach(courseId -> {
            bulkOps.updateOne(
                    Query.query(Criteria.where("_id").is(courseId)),
                    new Update().inc("studentsEnrolled", 1)
                            .set("updatedAt", Instant.now())
            );
        });
        bulkOps.execute();
    }
}

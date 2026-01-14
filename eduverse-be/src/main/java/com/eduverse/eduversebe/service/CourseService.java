package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.globalEnums.CourseStatus;
import com.eduverse.eduversebe.dto.respone.CourseResponse;
import com.eduverse.eduversebe.dto.respone.HomeContentResponse;
import com.eduverse.eduversebe.mapper.CourseMapper;
import com.eduverse.eduversebe.model.Category;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.repository.CategoryRepository;
import com.eduverse.eduversebe.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final CourseMapper courseMapper;

    public HomeContentResponse getHomeCourses() {
        // --- 1. Fetch Data từ DB ---

        // Newest: Sort CreatedAt DESC, Limit 8
        List<Course> newest = courseRepository.findByStatusAndIsPrivateFalseAndIsDeletedFalse(
                CourseStatus.Live,
                PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        // BestSellers: Sort StudentsEnrolled DESC, Limit 6
        List<Course> bestSellers = courseRepository.findByStatusAndIsPrivateFalseAndIsDeletedFalse(
                CourseStatus.Live,
                PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "studentsEnrolled"))
        );

        // TopRated: Sort Rating Average DESC, Limit 8
        // Lưu ý: Sort trong @Query repository hoặc truyền vào đây đều được
        List<Course> topRated = courseRepository.findTopRatedCourses(
                CourseStatus.Live,
                PageRequest.of(0, 8, Sort.by(Sort.Order.desc("rating.average"), Sort.Order.desc("rating.count")))
        );

        // BiggestDiscounts: Logic Aggregation phức tạp
        List<Course> biggestDiscounts = courseRepository.findBiggestDiscounts();


        // --- 2. Xử lý "Populate" Category thủ công (Tối ưu hiệu năng) ---
        // Gom tất cả các list lại để lấy Category ID 1 lần duy nhất
        List<Course> allCourses = new ArrayList<>();
        allCourses.addAll(newest);
        allCourses.addAll(bestSellers);
        allCourses.addAll(topRated);
        allCourses.addAll(biggestDiscounts);

        // Lấy danh sách ID category duy nhất (Distinct)
        Set<String> categoryIds = allCourses.stream()
                .map(Course::getCategoryId)
                .filter(Objects::nonNull) // Bỏ qua null
                .collect(Collectors.toSet());

        // Query DB Category 1 lần (SELECT * FROM category WHERE id IN (...))
        List<Category> categories = categoryRepository.findAllById(categoryIds);

        // Tạo Map để tra cứu nhanh: Map<CategoryId, CategoryObj>
        Map<String, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));


        // --- 3. Map Entity sang Response DTO kèm thông tin Category ---
        return HomeContentResponse.builder()
                .newest(mapToResponseList(newest, categoryMap))
                .bestSellers(mapToResponseList(bestSellers, categoryMap))
                .topRated(mapToResponseList(topRated, categoryMap))
                .biggestDiscounts(mapToResponseList(biggestDiscounts, categoryMap))
                .build();
    }

    // Helper function để map list Entity -> DTO
    private List<CourseResponse> mapToResponseList(List<Course> courses, Map<String, Category> categoryMap) {
        return courses.stream().map(course -> {
            // Map cơ bản (dùng MapStruct)
            CourseResponse res = courseMapper.toCourseResponse(course);

            // Map Category thủ công từ Map đã chuẩn bị
            if (course.getCategoryId() != null && categoryMap.containsKey(course.getCategoryId())) {
                Category cat = categoryMap.get(course.getCategoryId());
                // Gán thông tin category vào response
                // Giả sử CourseResponse có field categoryName/Slug hoặc nested object
                res.setCategoryName(cat.getName());
                res.setCategorySlug(cat.getSlug());
            }
            return res;
        }).collect(Collectors.toList());
    }

    public List<CourseResponse> getAllCoursesTest() {
        // 1. Lấy TẤT CẢ khóa học (Bỏ qua mọi bộ lọc Status, Private, Deleted...)
        List<Course> allCourses = courseRepository.findAll();

        // 2. Lấy danh sách Category ID để map tên
        Set<String> categoryIds = allCourses.stream()
                .map(Course::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<Category> categories = categoryRepository.findAllById(categoryIds);
        Map<String, Category> categoryMap = categories.stream()
                .collect(Collectors.toMap(Category::getId, c -> c));

        // 3. Map sang Response (Tái sử dụng hàm mapToResponseList đã viết trước đó)
        return mapToResponseList(allCourses, categoryMap);
    }
}

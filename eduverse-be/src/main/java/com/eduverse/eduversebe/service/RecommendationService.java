package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.globalEnums.CourseStatus;
import com.eduverse.eduversebe.common.globalEnums.OrderStatus;
import com.eduverse.eduversebe.common.model.BaseEntity;
import com.eduverse.eduversebe.common.utils.TfIdfCalculator;
import com.eduverse.eduversebe.dto.response.CourseResponse;
import com.eduverse.eduversebe.dto.response.RecommendationResponse;
import com.eduverse.eduversebe.mapper.CourseMapper;
import com.eduverse.eduversebe.model.*;
import com.eduverse.eduversebe.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OrderRepository orderRepository;
    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;

    // 2. Inject CourseMapper
    private final CourseMapper courseMapper;

    public RecommendationResponse getRecommendedCourses(String userId) {
        // --- PHẦN 1: LOGIC LẤY DATA & AI (GIỮ NGUYÊN KHÔNG ĐỔI) ---
        List<Order> orders = orderRepository.findByUserId(userId);
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(userId);
        User user = userRepository.findById(userId).orElse(null);

        List<Course> purchasedCourses = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.completed)
                .flatMap(o -> o.getCourses().stream())
                .map(item -> courseRepository.findById(item.getCourseId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<String> purchasedIds = purchasedCourses.stream().map(BaseEntity::getId).toList();

        List<Course> wishlistCourses = wishlistItems.stream()
                .map(w -> courseRepository.findById(w.getCourseId()).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        List<Course> historyCourses = new ArrayList<>(purchasedCourses);
        historyCourses.addAll(wishlistCourses);
        historyCourses = historyCourses.stream().filter(distinctByKey(BaseEntity::getId)).collect(Collectors.toList());

        List<String> userInterests = user != null ? user.getInterests() : new ArrayList<>();
        String debugSource = "None";
        List<Course> recommendedEntities = new ArrayList<>();

        // Logic AI TF-IDF
        Set<String> allCategoryIds = new HashSet<>();
        historyCourses.forEach(c -> { if(c.getCategoryId() != null) allCategoryIds.add(c.getCategoryId()); });

        List<Course> candidates = courseRepository.findAllByIsPrivateFalseAndIsDeletedFalseAndStatus(CourseStatus.Live);
        candidates.forEach(c -> { if(c.getCategoryId() != null) allCategoryIds.add(c.getCategoryId()); });

        Map<String, Category> categoryMap = categoryRepository.findAllById(allCategoryIds).stream()
                .collect(Collectors.toMap(Category::getId, c -> c, (a, b) -> a));

        if (!historyCourses.isEmpty() || !userInterests.isEmpty()) {
            if (!historyCourses.isEmpty() && !userInterests.isEmpty()) debugSource = "Hybrid(History + Interests)";
            else if (!historyCourses.isEmpty()) debugSource = "HistoryOnly";
            else debugSource = "InterestsOnly";

            StringBuilder profileBuilder = new StringBuilder();
            historyCourses.forEach(c -> profileBuilder.append(c.getTitle()).append(" "));
            userInterests.forEach(i -> profileBuilder.append(i).append(" "));
            historyCourses.forEach(c -> {
                if (c.getTags() != null) profileBuilder.append(String.join(" ", c.getTags())).append(" ");
                if (c.getCategoryId() != null && categoryMap.containsKey(c.getCategoryId()))
                    profileBuilder.append(categoryMap.get(c.getCategoryId()).getName()).append(" ");
            });

            String targetProfileText = profileBuilder.toString();
            List<Course> validCandidates = candidates.stream()
                    .filter(c -> !purchasedIds.contains(c.getId()))
                    .toList();

            TfIdfCalculator calculator = new TfIdfCalculator();
            calculator.addDocument(targetProfileText);

            for (Course c : validCandidates) {
                String catName = (c.getCategoryId() != null && categoryMap.containsKey(c.getCategoryId()))
                        ? categoryMap.get(c.getCategoryId()).getName() : "";
                String tags = c.getTags() != null ? String.join(" ", c.getTags()) : "";
                calculator.addDocument(c.getTitle() + " " + c.getSubtitle() + " " + catName + " " + tags);
            }

            Map<Course, Double> scores = new HashMap<>();
            for (int i = 0; i < validCandidates.size(); i++) {
                double score = calculator.calculateSimilarity(0, i + 1);
                if (score > 0) scores.put(validCandidates.get(i), score);
            }

            recommendedEntities = scores.entrySet().stream()
                    .sorted(Map.Entry.<Course, Double>comparingByValue().reversed())
                    .limit(8)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        }

        // Fallback
        if (recommendedEntities.isEmpty()) {
            debugSource = "Fallback(BestSellers)";
            recommendedEntities = courseRepository.findTop8ByIsPrivateFalseAndIsDeletedFalseAndStatusOrderByStudentsEnrolledDesc(CourseStatus.Live);

            Set<String> fallbackCatIds = recommendedEntities.stream()
                    .map(Course::getCategoryId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            List<String> missingIds = fallbackCatIds.stream().filter(id -> !categoryMap.containsKey(id)).toList();
            if(!missingIds.isEmpty()) {
                categoryRepository.findAllById(missingIds).forEach(c -> categoryMap.put(c.getId(), c));
            }
        }

        // --- PHẦN 2: CHỈNH SỬA MAP TO DTO (DÙNG MAPPER) ---

        List<CourseResponse> responseList = recommendedEntities.stream()
                .map(course -> {
                    // 1. Dùng Mapper để chuyển đổi cơ bản (ID, Title, Instructor, Rating...)
                    CourseResponse response = courseMapper.toCourseResponse(course);

                    // 2. Set bổ sung Tên Category (Vì Mapper đang ignore field này)
                    if (course.getCategoryId() != null && categoryMap.containsKey(course.getCategoryId())) {
                        if (response.getCategory() != null) {
                            response.getCategory().setName(categoryMap.get(course.getCategoryId()).getName());
                        }
                    }
                    return response;
                })
                .collect(Collectors.toList());

        return RecommendationResponse.builder()
                .success(true)
                .debugSource(debugSource)
                .courses(responseList)
                .build();
    }

    // Helper distinct (Giữ nguyên)
    private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new java.util.concurrent.ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}

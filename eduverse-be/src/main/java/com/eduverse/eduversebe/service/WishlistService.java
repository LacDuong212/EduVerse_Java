package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.respone.CourseResponse;
import com.eduverse.eduversebe.dto.respone.WishlistResponse;
import com.eduverse.eduversebe.mapper.CourseMapper;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.model.Wishlist;
import com.eduverse.eduversebe.repository.CourseRepository;
import com.eduverse.eduversebe.repository.WishlistRepository;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService{

    private final WishlistRepository wishlistRepository;
    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;

    public void addToWishlist(String userId, String courseId){

        if(!courseRepository.existsById(courseId)){
            throw new AppException(ErrorCodes.COURSE_NOT_FOUND);
        }

        try{
            Wishlist wishlist = Wishlist.builder()
                    .userId(userId)
                    .courseId(courseId)
                    .addedAt(Instant.now())
                    .build();

            wishlistRepository.save(wishlist);

        } catch (DuplicateKeyException e) {
            throw new AppException(ErrorCodes.COURSE_ALREADY_IN_WISHLIST);
        }
    }

    public void removeFromWishlist(String userId, String courseId){
        wishlistRepository.deleteByUserIdAndCourseId(userId, courseId);
    }

    public List<WishlistResponse> getWishlist(String userId) {
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(
                userId,
                Sort.by(Sort.Direction.DESC, "addedAt")
        );

        if (wishlistItems.isEmpty()) {
            return List.of();
        }

        List<String> courseIds = wishlistItems.stream()
                .map(Wishlist::getCourseId)
                .collect(Collectors.toList());

        List<Course> courses = courseRepository.findAllById(courseIds);

        Map<String, Course> courseMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        return wishlistItems.stream()
                .map(item -> {
                    Course courseEntity = courseMap.get(item.getCourseId());
                    if (courseEntity == null) return null;

                    CourseResponse courseDTO = courseMapper.toCourseResponse(courseEntity);

                    return WishlistResponse.builder()
                            .id(item.getId())
                            .userId(item.getUserId())
                            .addedAt(item.getAddedAt().atZone(ZoneId.systemDefault()).toInstant())
                            .course(courseDTO)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public boolean checkWishlist(String userId, String courseId) {
        return wishlistRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    public long countWishlist(String userId) {
        return wishlistRepository.countByUserId(userId);
    }
}

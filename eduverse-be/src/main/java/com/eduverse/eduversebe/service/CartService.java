package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.response.CartItemResponse;
import com.eduverse.eduversebe.dto.response.CourseResponse;
import com.eduverse.eduversebe.mapper.CourseMapper;
import com.eduverse.eduversebe.model.Cart;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.repository.CartRepository;
import com.eduverse.eduversebe.repository.CourseRepository;
import com.eduverse.eduversebe.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;

    private final CourseMapper courseMapper;

    public List<CartItemResponse> getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return populateCartItems(cart);
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .userId(userId)
                            .courses(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private List<CartItemResponse> populateCartItems(Cart cart) {
        if (cart.getCourses() == null || cart.getCourses().isEmpty()) {
            return Collections.emptyList();
        }

        // 1. Lấy danh sách ID các khóa học trong giỏ
        List<String> courseIds = cart.getCourses().stream()
                .map(Cart.CartItem::getCourseId)
                .collect(Collectors.toList());

        // 2. Query Database 1 lần để lấy thông tin các khóa học (Bulk Fetch)
        Map<String, Course> courseMap = courseRepository.findAllById(courseIds).stream()
                .collect(Collectors.toMap(Course::getId, Function.identity()));

        // 3. Map dữ liệu sang DTO Response
        return cart.getCourses().stream()
                .map(item -> {
                    Course course = courseMap.get(item.getCourseId());

                    // Nếu khóa học không tìm thấy (do đã bị xóa khỏi DB), bỏ qua item này
                    if (course == null) return null;

                    // Sử dụng Mapper để chuyển đổi Course Entity -> CourseResponse DTO
                    CourseResponse courseDTO = courseMapper.toCourseResponse(course);

                    return CartItemResponse.builder()
                            .course(courseDTO)
                            .addedAt(item.getAddedAt())
                            .build();
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<CartItemResponse> addToCart(String userId, String courseId) {
        Cart cart = getOrCreateCart(userId);

        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCodes.COURSE_NOT_FOUND);
        }

        boolean alreadyInCart = cart.getCourses().stream()
                .anyMatch(item -> item.getCourseId().equals(courseId));
        if (alreadyInCart) {
            throw new AppException(ErrorCodes.COURSE_ALREADY_IN_CART);
        }

        if (orderRepository.existsByUserIdAndCourseIdAndStatusCompleted(userId, courseId)) {
            throw new AppException(ErrorCodes.YOU_ALREADY_OWN_THIS_COURSE);
        }

        if (orderRepository.existsByUserIdAndCourseIdAndStatusPending(userId, courseId, Instant.now())) {
            throw new AppException(ErrorCodes.COURSE_IN_ACTIVE_ORDER);
        }

        Cart.CartItem newItem = Cart.CartItem.builder()
                .courseId(courseId)
                .addedAt(Instant.now())
                .build();

        cart.getCourses().add(newItem);
        cartRepository.save(cart);

        return populateCartItems(cart);
    }

    @Transactional
    public List<CartItemResponse> removeFromCart(String userId, String courseId) {
        Cart cart = getOrCreateCart(userId);

        // Filter để loại bỏ item có courseId tương ứng
        List<Cart.CartItem> newItems = cart.getCourses().stream()
                .filter(item -> !item.getCourseId().equals(courseId))
                .collect(Collectors.toList());

        cart.setCourses(newItems);
        cartRepository.save(cart);

        return populateCartItems(cart);
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.setCourses(new ArrayList<>());
        cartRepository.save(cart);
    }

    public int getCartCount(String userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> cart.getCourses().size())
                .orElse(0);
    }
}

package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.common.globalEnums.OrderStatus;
import com.eduverse.eduversebe.common.globalEnums.PaymentMethod;
import com.eduverse.eduversebe.dto.request.CreateOrderRequest;
import com.eduverse.eduversebe.dto.request.UpdateOrderRequest;
import com.eduverse.eduversebe.model.Cart;
import com.eduverse.eduversebe.model.Coupon;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.model.Order;
import com.eduverse.eduversebe.repository.CartRepository;
import com.eduverse.eduversebe.repository.CouponRepository;
import com.eduverse.eduversebe.repository.CourseRepository;
import com.eduverse.eduversebe.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CourseRepository courseRepository;
    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;
    private final OrderCompletionService orderCompletionService;

    public List<Order> getOrders(String userId) {
        return orderRepository.findByUserId(userId, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public Order getOrderById(String userId, String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCodes.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) {
            throw new AppException(ErrorCodes.UNAUTHORIZED_ACCESS);
        }
        return order;
    }

    @Transactional
    public Order createOrder(String userId, CreateOrderRequest request) {
        if (request.getCourseIds() == null || request.getCourseIds().isEmpty()) {
            throw new AppException(ErrorCodes.CART_EMPTY);
        }

        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(request.getPaymentMethod());
            if (paymentMethod != PaymentMethod.momo && paymentMethod != PaymentMethod.vnpay && paymentMethod != PaymentMethod.free) {
                throw new AppException(ErrorCodes.INVALID_PAYMENT_METHOD);
            }
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCodes.INVALID_PAYMENT_METHOD);
        }

        List<Course> dbCourses = courseRepository.findAllById(request.getCourseIds());
        List<Order.OrderItem> orderItems = new ArrayList<>();
        double subTotal = 0;

        for (Course course : dbCourses) {
            double priceToUse = (course.getDiscountPrice() != null && course.getDiscountPrice() < course.getPrice())
                    ? course.getDiscountPrice()
                    : course.getPrice();

            subTotal += priceToUse;

            orderItems.add(Order.OrderItem.builder()
                    .courseId(course.getId())
                    .pricePaid(priceToUse)
                    .build());
        }

        double discountAmount = 0;
        double finalAmount = subTotal;
        String couponId = null;
        Coupon couponDoc = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            couponDoc = couponRepository.findByCodeAndIsActiveTrue(request.getCouponCode().toUpperCase())
                    .orElseThrow(() -> new AppException(ErrorCodes.COUPON_NOT_FOUND));

            Instant now = Instant.now();
            if (couponDoc.getExpiryDate() != null && now.isAfter(couponDoc.getExpiryDate())) {
                throw new AppException(ErrorCodes.COUPON_EXPIRED);
            }
            if (couponDoc.getUsersUsed().contains(userId)) {
                throw new AppException(ErrorCodes.COUPON_ALREADY_USED);
            }

            discountAmount = Math.round((subTotal * couponDoc.getDiscountPercent()) / 100.0);
            finalAmount = subTotal - discountAmount;
            if (finalAmount < 0) finalAmount = 0;
            couponId = couponDoc.getId();
        }

        OrderStatus orderStatus = OrderStatus.pending;
        if (finalAmount == 0) {
            paymentMethod = PaymentMethod.free;
            orderStatus = OrderStatus.completed;
        }

        Order newOrder = Order.builder()
                .userId(userId)
                .courses(orderItems)
                .subTotal(subTotal)
                .couponId(couponId)
                .discountAmount(discountAmount)
                .totalAmount(finalAmount)
                .paymentMethod(paymentMethod)
                .status(orderStatus)
                .expiresAt(Instant.now().plusSeconds(15 * 60))
                .build();

        newOrder = orderRepository.save(newOrder);


        if (newOrder.getStatus() == OrderStatus.completed) {
            orderCompletionService.handleOrderCompleted(newOrder.getUserId(), request.getCourseIds());
        }

        if (couponDoc != null) {
            couponDoc.getUsersUsed().add(userId);
            couponRepository.save(couponDoc);
        }

        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart != null) {
            List<String> purchasedIds = request.getCourseIds();
            List<Cart.CartItem> remainingItems = cart.getCourses().stream()
                    .filter(item -> !purchasedIds.contains(item.getCourseId()))
                    .collect(Collectors.toList());

            cart.setCourses(remainingItems);
            cartRepository.save(cart);
        }

        return newOrder;
    }

    public Order updateOrder(String userId, String orderId, UpdateOrderRequest request) {
        Order order = getOrderById(userId, orderId);

        if ("cancelled".equalsIgnoreCase(request.getStatus())) {
            if (order.getStatus() != OrderStatus.pending) {
                throw new AppException(ErrorCodes.ORDER_NOT_CANCELLABLE);
            }
            order.setStatus(OrderStatus.cancelled);
            return orderRepository.save(order);
        }

        throw new AppException(ErrorCodes.UNAUTHORIZED_ACCESS);
    }

    public long countCompletedOrdersByCourseIds(List<String> courseIds) {
        if (courseIds == null) courseIds = List.of();
        return Optional.ofNullable(orderRepository.countCompletedOrdersByCourseIds(courseIds)).orElse(0L);
    }
}

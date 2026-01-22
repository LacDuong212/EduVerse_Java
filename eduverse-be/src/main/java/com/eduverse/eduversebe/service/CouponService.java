package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.request.ApplyCouponRequest;
import com.eduverse.eduversebe.dto.response.CouponCalculationResponse;
import com.eduverse.eduversebe.model.Coupon;
import com.eduverse.eduversebe.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public CouponCalculationResponse applyCoupon(String userId, ApplyCouponRequest request) {
        String code = request.getCode().toUpperCase();

        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new AppException(ErrorCodes.COUPON_NOT_FOUND));

        Instant now = Instant.now();

        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new AppException(ErrorCodes.COUPON_NOT_ACTIVE_YET);
        }

        if (coupon.getExpiryDate() != null && now.isAfter(coupon.getExpiryDate())) {
            throw new AppException(ErrorCodes.COUPON_NOT_ACTIVE_YET);
        }

        if (coupon.getUsersUsed().contains(userId)) {
            throw new AppException(ErrorCodes.COUPON_ALREADY_USED);
        }

        Double originalPrice = request.getOriginalPrice();

        double discountAmount = (originalPrice * coupon.getDiscountPercent()) / 100.0;
        double finalPrice = originalPrice - discountAmount;

        if (finalPrice < 0) finalPrice = 0;

        return CouponCalculationResponse.builder()
                .couponCode(coupon.getCode())
                .discountPercent(coupon.getDiscountPercent())
                .discountAmount(discountAmount)
                .newPrice(finalPrice)
                .build();
    }
}

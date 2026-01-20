package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.ApplyCouponRequest;
import com.eduverse.eduversebe.dto.respone.CouponCalculationResponse;
import com.eduverse.eduversebe.model.Coupon;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Coupon>>> getAllCoupons() {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_COUPONS_SUCCESS,
                couponService.getAllCoupons()
        ));
    }

    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<CouponCalculationResponse>> applyCoupon(
            @AuthenticationPrincipal User currentUser,
            @RequestBody ApplyCouponRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.APPLY_COUPONS_SUCCESS,
                couponService.applyCoupon(currentUser.getId(), request)
        ));
    }
}

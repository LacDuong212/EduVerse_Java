package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.response.CartItemResponse;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> getCart(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_CART_SUCCESS,
                cartService.getCart(currentUser.getId())
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> addToCart(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> body
    ) {
        String courseId = body.get("courseId");
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.ADDED_TO_CART,
                cartService.addToCart(currentUser.getId(), courseId)
        ));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<List<CartItemResponse>>> removeFromCart(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String courseId
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.REMOVED_FROM_CART,
                cartService.removeFromCart(currentUser.getId(), courseId)
        ));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearCart(
            @AuthenticationPrincipal User currentUser
    ) {
        cartService.clearCart(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.CLEAR_CART_SUCCESS));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> getCartCount(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_CART_COUNT_SUCCESS,
                cartService.getCartCount(currentUser.getId())
        ));
    }
}

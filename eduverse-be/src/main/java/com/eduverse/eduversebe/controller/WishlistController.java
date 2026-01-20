package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.respone.WishlistResponse;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> addToWishlist(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> requestBody
    ) {
        String courseId = requestBody.get("courseId");
        wishlistService.addToWishlist(currentUser.getId(), courseId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(SuccessCodes.ADDED_TO_WISHLIST));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<String>> removeFromWishlist(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String courseId
    ) {
        wishlistService.removeFromWishlist(currentUser.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.REMOVED_FROM_WISHLIST));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getWishlist(
            @AuthenticationPrincipal User currentUser
    ) {
        List<WishlistResponse> list = wishlistService.getWishlist(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.GET_WISHLIST_SUCCESS, list));
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkWishlist(
            @AuthenticationPrincipal User currentUser,
            @RequestParam String courseId
    ) {
        boolean exists = wishlistService.checkWishlist(currentUser.getId(), courseId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.CHECK_WISHLIST_SUCCESS, exists));
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> countWishlist(
            @AuthenticationPrincipal User currentUser
    ) {
        long count = wishlistService.countWishlist(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCodes.COUNT_WISHLIST_SUCCESS, count));
    }
}

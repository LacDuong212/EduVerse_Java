package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.CreateOrderRequest;
import com.eduverse.eduversebe.dto.request.UpdateOrderRequest;
import com.eduverse.eduversebe.model.Order;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getOrders(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_ORDERS_SUCCESS,
                orderService.getOrders(currentUser.getId())
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String id
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.GET_ORDER_BY_ID_SUCCESS,
                orderService.getOrderById(currentUser.getId(), id)
        ));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Order>> createOrder(
            @AuthenticationPrincipal User currentUser,
            @RequestBody CreateOrderRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.CREATE_ORDER_SUCCESS,
                orderService.createOrder(currentUser.getId(), request)
        ));
    }

    // PATCH /api/v1/orders/{id}/update
    @PatchMapping("/{id}/update")
    public ResponseEntity<ApiResponse<Order>> updateOrder(
            @AuthenticationPrincipal User currentUser,
            @PathVariable String id,
            @RequestBody UpdateOrderRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.UPDATE_ORDER_SUCCESS,
                orderService.updateOrder(currentUser.getId(), id, request)
        ));
    }
}

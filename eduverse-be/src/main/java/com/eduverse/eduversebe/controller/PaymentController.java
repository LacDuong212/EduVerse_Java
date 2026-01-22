package com.eduverse.eduversebe.controller;

import com.eduverse.eduversebe.common.api.ApiResponse;
import com.eduverse.eduversebe.common.config.PaymentConfig;
import com.eduverse.eduversebe.common.globalEnums.SuccessCodes;
import com.eduverse.eduversebe.dto.request.CreatePaymentRequest;
import com.eduverse.eduversebe.dto.response.PaymentResponse;
import com.eduverse.eduversebe.model.Order;
import com.eduverse.eduversebe.repository.OrderRepository;
import com.eduverse.eduversebe.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;
    private final PaymentConfig paymentConfig;

    // 1. Tạo thanh toán
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            HttpServletRequest request,
            @RequestBody CreatePaymentRequest paymentRequest
    ) {
        Order order = orderRepository.findById(paymentRequest.getOrderId()).orElse(null);
        String userId = order != null ? order.getUserId() : "";

        return ResponseEntity.ok(ApiResponse.success(
                SuccessCodes.CREATE_ORDER_SUCCESS,
                paymentService.createPayment(userId, paymentRequest.getOrderId(), paymentRequest.getPaymentMethod(), request)
        ));
    }

    // 2. MoMo IPN (Callback Background)
    @PostMapping("/momo/ipn")
    public ResponseEntity<Void> handleMomoIpn(@RequestBody Map<String, String> params) {
        boolean isValid = paymentService.verifyMomoSignature(params);
        if (!isValid) return ResponseEntity.badRequest().build();

        String orderId = params.get("orderId");
        int resultCode = Integer.parseInt(params.get("resultCode"));

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if (resultCode == 0) {
                paymentService.activateCoursesForOrder(order);
            } else {
                paymentService.cancelOrder(orderId);
            }
        }
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // 3. MoMo Return (Redirect User)
    @GetMapping("/momo/return")
    public void handleMomoReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        boolean isValid = paymentService.verifyMomoSignature(params);
        String orderId = params.get("orderId");
        String resultCode = params.get("resultCode");

        if (isValid && "0".equals(resultCode)) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) paymentService.activateCoursesForOrder(order);
        }

        String redirectUrl = isValid && "0".equals(resultCode)
                ? paymentConfig.getClientUrl() + "/student/payment-success?orderId=" + orderId + "&code=" + resultCode + "&gateway=momo"
                : paymentConfig.getClientUrl() + "/student/payment-failed?orderId=" + orderId + "&code=" + resultCode + "&gateway=momo";

        response.sendRedirect(redirectUrl);
    }

    // 4. VNPAY IPN
    @GetMapping("/vnpay/ipn")
    public ResponseEntity<Map<String, String>> handleVnpayIpn(@RequestParam Map<String, String> params) {
        boolean isValid = paymentService.verifyVnpaySignature(params);
        if (!isValid) return ResponseEntity.ok(Map.of("RspCode", "97", "Message", "Checksum failed"));

        String orderId = params.get("vnp_TxnRef");
        String rspCode = params.get("vnp_ResponseCode");

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return ResponseEntity.ok(Map.of("RspCode", "01", "Message", "Order not found"));

        if ("00".equals(rspCode)) {
            paymentService.activateCoursesForOrder(order);
        } else {
            paymentService.cancelOrder(orderId);
        }

        return ResponseEntity.ok(Map.of("RspCode", "00", "Message", "Success"));
    }

    // 5. VNPAY Return
    @GetMapping("/vnpay/return")
    public void handleVnpayReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        boolean isValid = paymentService.verifyVnpaySignature(params);
        String orderId = params.get("vnp_TxnRef");
        String rspCode = params.get("vnp_ResponseCode");

        if (isValid && "00".equals(rspCode)) {
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) paymentService.activateCoursesForOrder(order);
        }

        String redirectUrl = isValid && "00".equals(rspCode)
                ? paymentConfig.getClientUrl() + "/student/payment-success?orderId=" + orderId + "&code=" + rspCode + "&gateway=vnpay"
                : paymentConfig.getClientUrl() + "/student/payment-failed?orderId=" + orderId + "&code=" + rspCode + "&gateway=vnpay";

        response.sendRedirect(redirectUrl);
    }
}

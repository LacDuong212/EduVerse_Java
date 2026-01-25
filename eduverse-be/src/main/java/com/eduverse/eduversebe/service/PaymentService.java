package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.config.PaymentConfig;
import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.common.globalEnums.OrderStatus;
import com.eduverse.eduversebe.dto.response.PaymentResponse;
import com.eduverse.eduversebe.model.Course;
import com.eduverse.eduversebe.model.Order;
import com.eduverse.eduversebe.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentConfig paymentConfig;
    private final OrderRepository orderRepository;
    private final MongoTemplate mongoTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    public PaymentResponse createPayment(String userId, String orderId, String paymentMethod, HttpServletRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCodes.ORDER_NOT_FOUND));

        if (!order.getUserId().equals(userId)) throw new AppException(ErrorCodes.UNAUTHORIZED_ACCESS);
        if (order.getStatus() != OrderStatus.pending) throw new RuntimeException("Order is already " + order.getStatus());
        if (order.getExpiresAt() != null && Instant.now().isAfter(order.getExpiresAt())) {
            throw new RuntimeException("Order has expired");
        }

        long amount = Math.round(order.getTotalAmount());
        String orderInfo = "Payment for order " + orderId;

        if ("momo".equals(paymentMethod)) {
            return createMomoPayment(orderId, amount, orderInfo);
        } else if ("vnpay".equals(paymentMethod)) {
            String ipAddr = getIpAddress(request);
            return createVnpayPayment(orderId, amount, orderInfo, ipAddr);
        } else {
            throw new AppException(ErrorCodes.INVALID_PAYMENT_METHOD);
        }
    }

    private PaymentResponse createMomoPayment(String orderId, long amount, String orderInfo) {
        try {
            String requestId = orderId + "-" + System.currentTimeMillis();
            String requestType = "payWithMethod";
            String extraData = "";

            // Raw signature string construction (Strict order required by MoMo)
            String rawSignature = "accessKey=" + paymentConfig.getMomo().getAccessKey() +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + paymentConfig.getMomo().getIpnUrl() +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + paymentConfig.getMomo().getPartnerCode() +
                    "&redirectUrl=" + paymentConfig.getMomo().getRedirectUrl() +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            String signature = hmacSHA256(rawSignature, paymentConfig.getMomo().getSecretKey());

            Map<String, Object> map = new HashMap<>();
            map.put("partnerCode", paymentConfig.getMomo().getPartnerCode());
            map.put("requestId", requestId);
            map.put("amount", amount);
            map.put("orderId", orderId);
            map.put("orderInfo", orderInfo);
            map.put("redirectUrl", paymentConfig.getMomo().getRedirectUrl());
            map.put("ipnUrl", paymentConfig.getMomo().getIpnUrl());
            map.put("lang", "en");
            map.put("requestType", requestType);
            map.put("extraData", extraData);
            map.put("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(map, headers);

            JsonNode response = restTemplate.postForObject(paymentConfig.getMomo().getApiEndpoint(), entity, JsonNode.class);

            if (response != null && response.get("resultCode").asInt() == 0) {
                return PaymentResponse.builder()
                        .payUrl(response.get("payUrl").asText())
                        .deepLink(response.has("deeplink") ? response.get("deeplink").asText() : null)
                        .paymentMethod("momo")
                        .build();
            } else {
                throw new RuntimeException("MoMo Error: " + (response != null ? response.get("message").asText() : "Unknown"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating MoMo payment: " + e.getMessage());
        }
    }

    // --- 3. VNPAY HANDLERS ---
    private PaymentResponse createVnpayPayment(String orderId, long amount, String orderInfo, String ipAddr) {
        try {
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TxnRef = orderId;
            String vnp_TmnCode = paymentConfig.getVnpay().getTmnCode();

            Map<String, String> vnp_Params = new TreeMap<>(); // TreeMap tự động sort key (bắt buộc với VNPAY)
            vnp_Params.put("vnp_Version", vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPAY tính đơn vị đồng * 100
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_Locale", "en");
            vnp_Params.put("vnp_ReturnUrl", paymentConfig.getVnpay().getReturnUrl());
            vnp_Params.put("vnp_IpAddr", ipAddr);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
            vnp_Params.put("vnp_CreateDate", formatter.format(new Date()));

            // Build query String & Hash
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
                if (!hashData.isEmpty()) {
                    hashData.append('&');
                    query.append('&');
                }
                String encodedKey = URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII);
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII);

                hashData.append(encodedKey).append('=').append(encodedValue);
                query.append(encodedKey).append('=').append(encodedValue);
            }

            String queryUrl = query.toString();
            String vnp_SecureHash = hmacSHA512(hashData.toString(), paymentConfig.getVnpay().getHashSecret());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

            return PaymentResponse.builder()
                    .payUrl(paymentConfig.getVnpay().getUrl() + "?" + queryUrl)
                    .paymentMethod("vnpay")
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error creating VNPAY payment: " + e.getMessage());
        }
    }

    // --- 4. ACTIVATE COURSES (Bulk Write Logic) ---
    public void activateCoursesForOrder(Order order) {
        if (order.getStatus() != OrderStatus.pending) return;

        order.setStatus(OrderStatus.completed);
        order.setExpiresAt(null);
        orderRepository.save(order);

        // Đếm số lượng enrollment cho từng course
        Map<String, Integer> courseCounts = new HashMap<>();
        for (Order.OrderItem item : order.getCourses()) {
            courseCounts.put(item.getCourseId(), courseCounts.getOrDefault(item.getCourseId(), 0) + 1);
        }

        // Bulk Update trong MongoDB
        for (Map.Entry<String, Integer> entry : courseCounts.entrySet()) {
            mongoTemplate.updateFirst(
                    Query.query(Criteria.where("_id").is(entry.getKey())),
                    new Update().inc("studentsEnrolled", entry.getValue()),
                    Course.class
            );
        }
    }

    // --- 5. VERIFY SIGNATURES ---
    public boolean verifyMomoSignature(Map<String, String> params) {
        try {
            String rawSignature = "accessKey=" + paymentConfig.getMomo().getAccessKey() +
                    "&amount=" + params.get("amount") +
                    "&extraData=" + params.get("extraData") +
                    "&message=" + params.get("message") +
                    "&orderId=" + params.get("orderId") +
                    "&orderInfo=" + params.get("orderInfo") +
                    "&orderType=" + params.get("orderType") +
                    "&partnerCode=" + params.get("partnerCode") +
                    "&payType=" + params.get("payType") +
                    "&requestId=" + params.get("requestId") +
                    "&responseTime=" + params.get("responseTime") +
                    "&resultCode=" + params.get("resultCode") +
                    "&transId=" + params.get("transId");

            String signature = hmacSHA256(rawSignature, paymentConfig.getMomo().getSecretKey());
            return signature.equals(params.get("signature"));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyVnpaySignature(Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            params.remove("vnp_SecureHash");
            params.remove("vnp_SecureHashType");

            // Sort params
            Map<String, String> sortedParams = new TreeMap<>(params);
            StringBuilder hashData = new StringBuilder();

            for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
                if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                    if (!hashData.isEmpty()) hashData.append('&');
                    hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII));
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
                }
            }

            String signed = hmacSHA512(hashData.toString(), paymentConfig.getVnpay().getHashSecret());
            return signed.equals(vnp_SecureHash);
        } catch (Exception e) {
            return false;
        }
    }

    // --- UTILS ---
    private String hmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        return HexFormat.of().formatHex(sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String hmacSHA512(String data, String key) throws Exception {
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512_HMAC.init(secret_key);
        return HexFormat.of().formatHex(sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    // Helper để hủy đơn nếu thanh toán lỗi
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null && order.getStatus() == OrderStatus.pending) {
            order.setStatus(OrderStatus.cancelled);
            order.setExpiresAt(null);
            orderRepository.save(order);
        }
    }
}

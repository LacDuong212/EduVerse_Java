package com.eduverse.eduversebe.common.filter;

import com.eduverse.eduversebe.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Lấy Token từ request (Ưu tiên Cookie trước, sau đó đến Header)
        String jwt = getTokenFromRequest(request);
        final String userEmail;

        // Nếu không có token -> Cho qua (để các API public hoạt động hoặc báo lỗi 403 sau)
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Trích xuất Email từ Token
            userEmail = jwtService.extractUsername(jwt);

            // 3. Nếu có Email và chưa được xác thực trong Context hiện tại
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Lấy thông tin user từ DB
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

                // 4. Kiểm tra Token có hợp lệ với User này không
                if (jwtService.isTokenValid(jwt, userDetails)) {

                    // Tạo đối tượng Authentication
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 5. CẬP NHẬT SECURITY CONTEXT (Đánh dấu là đã đăng nhập)
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Nếu token lỗi (hết hạn, sai chữ ký...), ta cứ log ra và để filter chain chạy tiếp
            // Spring Security sẽ tự chặn ở bước sau nếu API đó yêu cầu auth
            log.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Hàm hỗ trợ lấy Token từ Cookie hoặc Header
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Cách 1: Lấy từ Cookie (Cách bạn đang dùng bên Node.js)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Cách 2: Lấy từ Header Authorization (Dự phòng cho Postman/Mobile)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }
}
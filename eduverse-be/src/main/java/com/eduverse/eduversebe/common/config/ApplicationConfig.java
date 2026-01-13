package com.eduverse.eduversebe.common.config;

import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.model.User; // User của bạn
import com.eduverse.eduversebe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        // Trả về một lambda expression
        return username -> {

            // 1. Tìm User trong DB của bạn (User Model gốc)
            User user = userRepository.findById(username)
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

            // 2. Chuyển đổi (Map) sang User của Spring Security
            // Lưu ý: org.springframework.security.core.userdetails.User là class có sẵn của Spring
            return new org.springframework.security.core.userdetails.User(
                    user.getId(),// Username
                    user.getPassword(),       // Password đã hash
                    user.isActivated(),       // Enabled (Kích hoạt)
                    true,                     // Account Non Expired
                    true,                     // Credentials Non Expired
                    true,
                    Collections.singletonList( // Danh sách quyền (Role)
                            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                    )
            );
        };
    }
}
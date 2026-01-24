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

        return username -> {

            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

            return new org.springframework.security.core.userdetails.User(
                    user.getId(),
                    user.getPassword(),
                    user.isActivated(),
                    true,
                    true,
                    true,
                    Collections.singletonList(
                            new SimpleGrantedAuthority(user.getRole().asAuthority())
                    )
            );
        };
    }
}
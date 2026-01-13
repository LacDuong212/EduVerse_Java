package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.exception.AppException;
import com.eduverse.eduversebe.common.globalEnums.ErrorCodes;
import com.eduverse.eduversebe.dto.request.*;
import com.eduverse.eduversebe.dto.respone.LoginResponse;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;;
    private final PasswordEncoder  passwordEncoder;
    private final EmailService emailService;
    private final JwtService jwtService;

    private String generateOtp() {
        int randomPin = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(randomPin);
    }

    @Transactional
    public void register(RegisterRequest request) {
        String cleanEmail = request.getEmail().toLowerCase().trim();

        Optional<User> existingUserOpt = userRepository.findByEmail(cleanEmail);
        User userToSave;

        if(existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();

            if(existingUser.isVerified()) {
                throw new AppException(ErrorCodes.USER_EXISTED);
            }

            log.info(">>> [AUTH]: Resending verification for unverified user: {}", cleanEmail);
            userToSave = existingUser;
            userToSave.setName(request.getName());
        } else {
            userToSave = new User();
            userToSave.setEmail(cleanEmail);
            userToSave.setName(request.getName());
            userToSave.setVerified(false);
            userToSave.setActivated(true);
        }

        userToSave.setPassword(passwordEncoder.encode(request.getPassword()));

        String otp = generateOtp();

        userToSave.setVerifyOtp(otp);
         userToSave.setVerifyOtpExpireAt(System.currentTimeMillis() + 10 * 60 * 1000);

         userRepository.save(userToSave);

         emailService.sendOtpEmail(userToSave.getEmail(), userToSave.getName(), otp);
    }

    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {
        String cleanEmail = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        if (user.isVerified()) {
            throw new AppException(ErrorCodes.USER_ALREADY_VERIFIED);
        }

        if (!user.getVerifyOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCodes.INVALID_OTP);
        }

        if (user.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new AppException(ErrorCodes.OTP_EXPIRED);
        }

        user.setVerified(true);
        user.setVerifyOtp("");
        user.setVerifyOtpExpireAt(0L);

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        String cleanEmail = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        if (!user.isActivated()) {
            throw new AppException(ErrorCodes.USER_IS_BLOCKED);
        }

        if(!user.isVerified()) {
            throw new AppException(ErrorCodes.USER_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCodes.LOGIN_FAILED);
        }

        String token  = jwtService.generateToken(user.getId());

        return LoginResponse.builder()
                .accessToken(token)
                ._id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .avatar(user.getAvatar())
                .interests(user.getInterests())
                .build();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        String cleanEmail = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        if (!user.isVerified()) {
            throw new AppException(ErrorCodes.USER_NOT_VERIFIED);
        }

        String otp = generateOtp();

        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(System.currentTimeMillis() + (10 * 60 * 1000));

        userRepository.save(user);

        emailService.sendForgotPasswordEmail(user.getEmail(), user.getName(), otp);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String cleanEmail = request.getEmail().toLowerCase().trim();

        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        if (!user.isVerified()) {
            throw new AppException(ErrorCodes.USER_NOT_VERIFIED);
        }

        if (!user.getVerifyOtp().equals(request.getOtp())) {
            throw new AppException(ErrorCodes.INVALID_OTP);
        }

        if (user.getVerifyOtpExpireAt() < System.currentTimeMillis()) {
            throw new AppException(ErrorCodes.OTP_EXPIRED);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        user.setVerifyOtp("");
        user.setVerifyOtpExpireAt(0L);

        userRepository.save(user);
    }

    @Transactional
    public void sendOtp(SendOtpRequest request) {
        String cleanEmail = request.getEmail().toLowerCase().trim();

        // 1. Tìm user
        User user = userRepository.findByEmail(cleanEmail)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND));

        // 2. Kiểm tra đã Verify chưa (Ngược lại với Forgot Password)
        // Node.js: if (user.isVerified) return error
        if (user.isVerified()) {
            throw new AppException(ErrorCodes.USER_ALREADY_VERIFIED);
        }

        // 3. Tạo OTP mới
        String otp = generateOtp(); // Hàm này bạn đã viết ở bài ForgotPassword rồi

        // 4. Lưu vào DB
        user.setVerifyOtp(otp);
        user.setVerifyOtpExpireAt(System.currentTimeMillis() + (10 * 60 * 1000)); // 10 phút

        userRepository.save(user);

        // 5. Gửi mail
        emailService.sendVerificationEmail(user.getEmail(), user.getName(), otp);
    }
}
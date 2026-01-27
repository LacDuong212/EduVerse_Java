package com.eduverse.eduversebe.service;

import com.eduverse.eduversebe.common.globalEnums.UserRole;
import com.eduverse.eduversebe.model.User;
import com.eduverse.eduversebe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final StudentService studentService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        Optional<User> userOptional = userRepository.findByGoogleId(googleId);

        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            Optional<User> userByEmail = userRepository.findByEmail(email);

            if (userByEmail.isPresent()) {
                user = userByEmail.get();
                user.setGoogleId(googleId);
                if (user.getPfpImg() == null || user.getPfpImg().isEmpty()) {
                    user.setPfpImg(picture);
                }
                userRepository.save(user);
            } else {
                user = User.builder()
                        .name(name)
                        .email(email)
                        .googleId(googleId)
                        .pfpImg(picture)
                        .isVerified(true)
                        .isActivated(true)
                        .role(UserRole.student)
                        .build();
                User savedUser = userRepository.save(user);

                if (!studentService.existsByUserId(savedUser.getId())) {
                    studentService.createNewStudent(savedUser.getId());
                }
            }
        }

        return oAuth2User;
    }
}

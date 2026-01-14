package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.globalEnums.UserRole;
import com.eduverse.eduversebe.common.model.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class User extends BaseEntity {

    private String name;

    @Indexed(unique = true)
    private String email;

    @Field("phonenumber")
    @Builder.Default
    private String phoneNumber = "";

    @Builder.Default
    private String bio = "";

    @Builder.Default
    private String website = "";

    @Builder.Default
    private Socials socials =  new Socials();

    @Field("pfpImg")
    @Builder.Default
    private String avatar = "";

    private String password;

    @Builder.Default
    private String verifyOtp = "";

    @Builder.Default
    private Long verifyOtpExpireAt = 0L;

    @Builder.Default
    private boolean isVerified = false;

    @Builder.Default
    private boolean isActivated = true;

    @Builder.Default
    private List<String> interests = new ArrayList<>();

    @Builder.Default
    private UserRole role = UserRole.student;

    @Indexed(unique = true, sparse = true)
    private String googleId;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Socials {
        @Builder.Default
        private String facebook = "";
        @Builder.Default
        private String instagram = "";
        @Builder.Default
        private String linkedin = "";
        @Builder.Default
        private String youtube = "";
    }
}

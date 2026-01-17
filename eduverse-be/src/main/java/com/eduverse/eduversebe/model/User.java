package com.eduverse.eduversebe.model;

import com.eduverse.eduversebe.common.globalEnums.UserRole;
import com.eduverse.eduversebe.common.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Document(collection = "users")
public class User extends BaseEntity implements UserDetails {

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
    private Socials socials = new Socials();

    @Builder.Default
    private String pfpImg = "";

    @JsonIgnore
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name().toUpperCase()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isActivated;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isVerified;
    }

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

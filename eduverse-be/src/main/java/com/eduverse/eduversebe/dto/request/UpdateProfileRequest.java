package com.eduverse.eduversebe.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String name;
    private String phoneNumber;
    private String bio;
    private String website;
    private String avatar;
    private List<String> interests;

    private SocialsRequest socials;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SocialsRequest {
        private String facebook;
        private String instagram;
        private String linkedin;
        private String youtube;
    }
}

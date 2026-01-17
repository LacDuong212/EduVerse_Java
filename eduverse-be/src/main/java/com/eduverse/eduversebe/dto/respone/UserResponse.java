package com.eduverse.eduversebe.dto.respone;

import com.eduverse.eduversebe.common.globalEnums.UserRole;
import com.eduverse.eduversebe.model.User;
import lombok.*;
import java.util.List;

@Data
@Builder
public class UserResponse {
    private String _id;
    private String name;
    private String email;
    private String phoneNumber;
    private String bio;
    private String website;
    private User.Socials socials;
    private String pfpImg;
    private Boolean isVerified;
    private UserRole role;
    private List<String> interests;
}
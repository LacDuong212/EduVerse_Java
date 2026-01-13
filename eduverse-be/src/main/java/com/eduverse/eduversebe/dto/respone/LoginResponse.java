package com.eduverse.eduversebe.dto.respone;

import com.eduverse.eduversebe.common.globalEnums.UserRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LoginResponse {
    private String accessToken;

    private String _id;
    private String name;
    private String email;
    private UserRole role;
    private String avatar;
    private List<String> interests;
}

package com.eduverse.eduversebe.mapper;

import com.eduverse.eduversebe.dto.request.UpdateProfileRequest;
import com.eduverse.eduversebe.dto.respone.UserResponse;
import com.eduverse.eduversebe.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "_id")
    @Mapping(source = "verified", target = "isVerified")
    UserResponse toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User user, UpdateProfileRequest request);
}

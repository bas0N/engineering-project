package org.example.auth.mapper;

import org.example.auth.dto.request.UserRegisterRequest;
import org.example.auth.dto.response.UserAdminDetailsResponse;
import org.example.auth.dto.response.UserDetailsResponse;
import org.example.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "active", constant = "true")
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "lastName", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "firstName", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    @Mapping(source = "dto.email", target = "email")
    @Mapping(source = "dto.password", target = "password")
    @Mapping(constant = "USER", target = "role")
    @Mapping(constant = "false", target = "lock")
    @Mapping(constant = "true", target = "enabled")
    User mapUserRegisterDtoToUser(UserRegisterRequest dto);

    @Mapping(target = "lock", source = "isLock")
    @Mapping(target = "id", source = "id")
    UserAdminDetailsResponse toUserAdminDetailsResponse(User user);

    @Mapping(target = "id", source = "uuid")
    UserDetailsResponse toUserDetailsResponse(User user);
}

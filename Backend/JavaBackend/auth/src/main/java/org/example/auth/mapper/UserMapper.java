package org.example.auth.mapper;

import org.example.auth.dto.UserRegisterRequest;
import org.example.auth.entity.User;
import org.example.auth.entity.UserVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "dto.email", target = "email")
    @Mapping(source = "dto.password", target = "password")
    @Mapping(constant = "USER", target = "role")
    @Mapping(constant = "false", target = "lock")
    @Mapping(constant = "true", target = "enabled")
    User mapUserRegisterDtoToUser(UserRegisterRequest dto);


    @Mapping(ignore = true, target = "id")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "addresses", target = "addressVersions")
    @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "versionTimestamp")
    UserVersion toUserVersion(User user);
}

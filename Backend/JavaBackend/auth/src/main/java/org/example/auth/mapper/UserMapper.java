package org.example.auth.mapper;

import org.example.auth.dto.UserRegisterRequest;
import org.example.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "dto.email", target = "email")
    @Mapping(source = "dto.password", target = "password")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "lock", constant = "false")
    @Mapping(target = "enabled", constant = "true")
    User mapUserRegisterDtoToUser(UserRegisterRequest dto);
}

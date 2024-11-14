package org.example.product.mapper;

import org.example.commondto.UserDetailInfoEvent;
import org.example.product.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    User toUser(UserDetailInfoEvent userDetailInfoEvent);
}

package org.example.message.mapper;

import org.example.commondto.UserDetailInfoEvent;
import org.example.message.dto.response.UserDetailsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "username", expression = "java(concatenateFullName(userDetailInfoEvent.getFirstName(), userDetailInfoEvent.getLastName()))")
    UserDetailsResponse toUserDetailsResponse(UserDetailInfoEvent userDetailInfoEvent);

    default String concatenateFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }
}

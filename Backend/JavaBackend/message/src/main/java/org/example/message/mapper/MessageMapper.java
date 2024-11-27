package org.example.message.mapper;

import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.MessageResponse;
import org.example.message.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.time.ZoneId;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);


    MessageResponse toMessageResponse(Message message);



    @Mapping(target = "read", expression = "java(false)")
    @Mapping(target = "uuid", expression = "java(generateUuid())")
    @Mapping(target = "dateAdded", expression = "java(getCurrentDateTime())")
    @Mapping(target = "id", ignore = true)
    Message toMessage(MessageRequest messageRequest, String senderId);

    List<MessageResponse> toMessageResponseList(List<Message> messages);

    default String generateUuid() {
        return UUID.randomUUID().toString();
    }

    default Date getCurrentDateTime() {
        return Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
    }
}

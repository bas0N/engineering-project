package org.example.message.mapper;

import org.example.message.dto.MessageResponse;
import org.example.message.entity.Message;
import org.example.message.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    @Mapping(target = "receiver", source = "message.receiverId")
    @Mapping(target = "sender", source = "message.senderId")
    MessageResponse toMessageResponse(Message message);

    @Mapping(target = "receiverId", source = "receiver")
    @Mapping(target = "senderId", source = "sender")
    Message toMessage(MessageResponse messageResponse, User Sender, User receiver);
}

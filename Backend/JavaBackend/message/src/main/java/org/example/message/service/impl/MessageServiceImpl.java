package org.example.message.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.message.dto.MessageRequest;
import org.example.message.dto.MessageResponse;
import org.example.message.entity.Message;
import org.example.message.mapper.MessageMapper;
import org.example.message.repository.MessageRepository;
import org.example.message.repository.UserRepository;
import org.example.message.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final JwtCommonService jwtCommonService;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public List<MessageResponse> getMessages(String contactId, HttpServletRequest request) {
        String currentUserId = jwtCommonService.getUserFromRequest(request);
        List<Message> messages = messageRepository.findMessagesBySender_IdAndReceiver_Id(currentUserId, contactId);
        return messages.stream().map(MessageMapper.INSTANCE::toMessageResponse).toList();
    }

    @Override
    public MessageResponse createMessage(MessageRequest messageRequest, HttpServletRequest request) {
        String senderId = jwtCommonService.getUserFromRequest(request);
        if(userRepository.findUserByUuid(messageRequest.getReceiverId()).isEmpty()) {
            //kafka
        }
        if(userRepository.findUserByUuid(senderId).isEmpty()) {
            //kafka
        }
        return null;
    }
}

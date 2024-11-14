package org.example.message.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.message.dto.MessageRequest;
import org.example.message.dto.MessageResponse;
import org.example.message.entity.Message;
import org.example.message.entity.User;
import org.example.message.mapper.MessageMapper;
import org.example.message.repository.MessageRepository;
import org.example.message.repository.UserRepository;
import org.example.message.service.MessageService;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final JwtCommonService jwtCommonService;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;


    @Override
    public List<MessageResponse> getMessages(String contactId, String currentUserId) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(currentUserId, contactId);
        return messages.stream().map(MessageMapper.INSTANCE::toMessageResponse).toList();
    }


    @Override
    public MessageResponse createMessage(MessageRequest messageRequest, String senderId) {
        Optional<User> senderOpt = userRepository.findUserByUuid(senderId);
        Optional<User> receiverOpt = userRepository.findUserByUuid(messageRequest.getReceiverId());

        if (senderOpt.isEmpty() || receiverOpt.isEmpty()) {
            // Komunikacja z Kafka, jeśli użytkownik nie istnieje
            // Możesz tutaj zaimplementować pobieranie użytkownika z innego modułu za pomocą Kafka
            // Dla uproszczenia zakładamy, że użytkownik musi istnieć w bazie
            throw new RuntimeException("User not found");
        }

        User sender = senderOpt.get();
        User receiver = receiverOpt.get();

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setDateAdded(new Date());

        messageRepository.save(message);

        return MessageMapper.INSTANCE.toMessageResponse(message);
    }

    @Override
    public void deleteMessage(String messageId, String currentUserId) {
        Message message = messageRepository.findByUuid(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        if (!message.getSender().getUuid().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to delete this message");
        }

        messageRepository.delete(message);
    }
}

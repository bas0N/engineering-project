package org.example.message.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.commondto.UserDetailInfoEvent;
import org.example.jwtcommon.jwt.JwtCommonService;
import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.ChatResponse;
import org.example.message.dto.response.MessageResponse;
import org.example.message.dto.response.MessagesResponse;
import org.example.message.dto.response.UserDetailsResponse;
import org.example.message.entity.Message;
import org.example.message.mapper.MessageMapper;
import org.example.message.mapper.UserMapper;
import org.example.message.repository.MessageRepository;
import org.example.message.service.MessageService;
import org.example.message.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;


    @Override
    public MessagesResponse getMessages(String contactId, String currentUserId) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(currentUserId, contactId);
        List<Message> unreadMessages = messages.stream()
                .filter(message -> message.getReceiverId().equals(currentUserId) && !message.isRead())
                .collect(Collectors.toList());

        if (!unreadMessages.isEmpty()) {
            unreadMessages.forEach(message -> message.setRead(true));
            messageRepository.saveAll(unreadMessages);
        }

        UserDetailInfoEvent receiver = userService.getUserInfo(contactId);
        UserDetailInfoEvent sender = userService.getUserInfo(currentUserId);
        if(receiver == null || sender == null) {
            throw new RuntimeException("User not found");
        }
        return new MessagesResponse(MessageMapper.INSTANCE.toMessageResponseList(messages), UserMapper.INSTANCE.toUserDetailsResponse(sender), UserMapper.INSTANCE.toUserDetailsResponse(receiver));
    }

    @Override
    public Page<ChatResponse> getChats(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "lastMessageTime"));
        return messageRepository.findChats(name, pageable);
    }

    @Override
    public void markMessagesAsRead(List<String> messageIds, String currentUserId) {
        List<Message> messages = messageRepository.findAllByUuidInAndReceiverId(messageIds, currentUserId);
        if (messages.isEmpty()) {
            throw new RuntimeException("No messages found or you are not authorized to mark them as read");
        }
        messages.forEach(message -> message.setRead(true));
        messageRepository.saveAll(messages);

        // Optionally, notify the sender(s) about read status
//        messages.forEach(message -> {
//            messagingTemplate.convertAndSendToUser(
//                    message.getSenderId(),
//                    "/queue/read-receipts",
//                    new ReadReceipt(message.getUuid(), currentUserId)
//            );
//        });
    }


    @Override
    public MessageResponse createMessage(MessageRequest messageRequest, String senderId) {
        Message message = MessageMapper.INSTANCE.toMessage(messageRequest, senderId);
        messageRepository.saveAndFlush(message);
        return MessageMapper.INSTANCE.toMessageResponse(message);
    }

    @Override
    public void deleteMessage(String messageId, String currentUserId) {
        Message message = messageRepository.findByUuid(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to delete this message");
        }
        messageRepository.delete(message);
    }
}

package org.example.message.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.commondto.UserDetailInfoEvent;
import org.example.commonutils.Utils;
import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.ChatResponse;
import org.example.message.dto.response.MessageResponse;
import org.example.message.dto.response.MessagesResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final Utils utils;


    @Override
    public MessagesResponse getMessages(String contactId, HttpServletRequest request) {
        String currentUserId = utils.extractUserIdFromRequest(request);
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
    public List<ChatResponse> getChats(HttpServletRequest request) {
        String userId = utils.extractUserIdFromRequest(request);

        // 1. Pobierz listę partnerów
        List<String> partners = messageRepository.findChatPartners(userId);

        List<ChatResponse> result = new ArrayList<>();
        for (String partnerId : partners) {
            // 2. Pobierz ostatnią wiadomość w czacie userId <-> partnerId
            List<Message> lastMsgList = messageRepository.findLastMessage(
                    userId, partnerId, PageRequest.of(0, 1)
            );
            if (lastMsgList.isEmpty()) {
                // Brak wiadomości? Teoretycznie nie powinno się zdarzyć, skoro partnerId jest na liście
                continue;
            }
            Message lastMessageEntity = lastMsgList.get(0);

            // 3. Policz nieprzeczytane
            long unreadCount = messageRepository.countUnreadMessages(userId, partnerId);

            // 4. Zbuduj ChatResponse
            ChatResponse chatResp = new ChatResponse();
            chatResp.setReceiverId(partnerId);
            chatResp.setLastMessage(lastMessageEntity.getContent());
            chatResp.setLastMessageTime(lastMessageEntity.getDateAdded().toString());
            chatResp.setRead(lastMessageEntity.isRead());
            chatResp.setUnreadCount((int) unreadCount);

            // 5. Opcjonalnie pobierz info o userze z userService
            UserDetailInfoEvent userDetails = userService.getUserInfo(partnerId);
            if (userDetails != null) {
                chatResp.setEmail(userDetails.getEmail());
                chatResp.setUsername(userDetails.getFirstName() + " " + userDetails.getLastName());
            }

            result.add(chatResp);
        }

        // posortuj w pamięci malejąco po lastMessageTime (o ile chcesz)
        result.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));
        return result;
    }

    @Override
    public void markMessagesAsRead(List<String> messageIds, HttpServletRequest request) {
        String currentUserId = utils.extractUserIdFromRequest(request);
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
    public int countUnreadMessages(HttpServletRequest request) {
        String name = utils.extractUserIdFromRequest(request);
        return messageRepository.countAllByReceiverIdAndReadFalse(name);
    }


    @Override
    public MessageResponse createMessage(MessageRequest messageRequest, String senderId) {
        Message message = MessageMapper.INSTANCE.toMessage(messageRequest, senderId);
        messageRepository.saveAndFlush(message);
        return MessageMapper.INSTANCE.toMessageResponse(message);
    }

    @Override
    public void deleteMessage(String messageId, HttpServletRequest request) {
        String currentUserId = utils.extractUserIdFromRequest(request);
        Message message = messageRepository.findByUuid(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getSenderId().equals(currentUserId)) {
            throw new RuntimeException("You are not authorized to delete this message");
        }
        messageRepository.delete(message);
    }
}

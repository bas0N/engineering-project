package org.example.message.service;

import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.ChatResponse;
import org.example.message.dto.response.MessageResponse;
import org.example.message.dto.response.MessagesResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MessageService {

    void deleteMessage(String messageId, String currentUserId);

    MessageResponse createMessage(MessageRequest messageRequest, String name);

    MessagesResponse getMessages(String contactId, String name);

    Page<ChatResponse> getChats(String name, int page, int size);

    void markMessagesAsRead(List<String> messageIds, String name);
}

package org.example.message.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.ChatResponse;
import org.example.message.dto.response.MessageResponse;
import org.example.message.dto.response.MessagesResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MessageService {

    void deleteMessage(String messageId,  HttpServletRequest request);

    MessageResponse createMessage(MessageRequest messageRequest, String userId);

    MessagesResponse getMessages(String contactId, HttpServletRequest request);

    List<ChatResponse> getChats( HttpServletRequest request);

    void markMessagesAsRead(List<String> messageIds, HttpServletRequest request);

    int countUnreadMessages(HttpServletRequest request);
}

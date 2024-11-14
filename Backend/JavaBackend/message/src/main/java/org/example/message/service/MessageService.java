package org.example.message.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.message.dto.MessageRequest;
import org.example.message.dto.MessageResponse;

import java.security.Principal;
import java.util.List;

public interface MessageService {

    void deleteMessage(String messageId, String currentUserId);

    MessageResponse createMessage(MessageRequest messageRequest, String name);

    List<MessageResponse> getMessages(String contactId, String name);
}

package org.example.message.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.message.dto.MessageRequest;
import org.example.message.dto.MessageResponse;

import java.util.List;

public interface MessageService {

    List<MessageResponse> getMessages(String userId, HttpServletRequest request);

    MessageResponse createMessage(MessageRequest messageRequest, HttpServletRequest request);
}

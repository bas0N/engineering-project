package org.example.message.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.message.dto.MessageRequest;
import org.example.message.dto.MessageResponse;
import org.example.message.dto.UserResponse;
import org.example.message.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/message")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/sendMessage")
    public void sendMessage(MessageRequest messageRequest, HttpServletRequest request) {
        MessageResponse response = messageService.createMessage(messageRequest, request);
        messagingTemplate.convertAndSendToUser(
                response.getReceiver().getId(),
                "/queue/messages",
                response
        );
    }

    @RequestMapping(path = "/{contactId}", method = RequestMethod.GET)
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable String contactId, HttpServletRequest request) {
        List<MessageResponse> messages = messageService.getMessages(contactId, request);
        return ResponseEntity.ok(messages);
    }

    @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId, HttpServletRequest request) {
        return null;
    }

}

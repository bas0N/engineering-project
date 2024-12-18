package org.example.message.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.commonutils.Utils;
import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.ChatResponse;
import org.example.message.dto.response.MessageResponse;
import org.example.message.dto.response.MessagesResponse;
import org.example.message.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/message")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Utils utils;

    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload MessageRequest messageRequest, org.springframework.messaging.Message<?> stompMessage) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(stompMessage);

        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || authHeader.isBlank()) {
            throw new RuntimeException("Authorization token is missing in STOMP headers");
        }

        if (!authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header is missing Bearer prefix");
        }

        String jwtToken = authHeader.substring(7);

        String userId;
        try {
            userId = utils.extractUserIdFromToken(jwtToken);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
        MessageResponse response = messageService.createMessage(messageRequest, userId);
        messagingTemplate.convertAndSendToUser(
                response.getReceiverId(),
                "/queue/messages",
                response
        );

        messagingTemplate.convertAndSendToUser(
                response.getSenderId(),
                "/queue/messages",
                response
        );
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<MessagesResponse> getMessages(@PathVariable String contactId, HttpServletRequest request) {
        return ResponseEntity.ok(messageService.getMessages(contactId, request));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId, HttpServletRequest request) {
        messageService.deleteMessage(messageId, request);
        return ResponseEntity.ok("Message deleted successfully");
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatResponse>> getChats(HttpServletRequest request) {
        return ResponseEntity.ok(messageService.getChats(request));
    }

    @PostMapping("/markAsRead")
    public ResponseEntity<Void> markMessagesAsRead(@RequestBody List<String> messageIds,  HttpServletRequest request) {
        messageService.markMessagesAsRead(messageIds, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unreadCount")
    public ResponseEntity<Integer> getUnreadCount(HttpServletRequest request) {
        int unreadCount = messageService.countUnreadMessages(request);
        return ResponseEntity.ok(unreadCount);
    }


}

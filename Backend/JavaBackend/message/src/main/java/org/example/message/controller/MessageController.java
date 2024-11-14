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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/message")
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload MessageRequest messageRequest, Principal principal) {
        MessageResponse response = messageService.createMessage(messageRequest, principal.getName());
        // Wysłanie wiadomości do odbiorcy
        messagingTemplate.convertAndSendToUser(
                response.getReceiver().getId(),
                "/queue/messages",
                response
        );
//        // Opcjonalnie: Wysłanie wiadomości do nadawcy (potwierdzenie)
//        messagingTemplate.convertAndSendToUser(
//                response.getSender().getId(),
//                "/queue/messages",
//                response
//        );
    }

    @GetMapping("/{contactId}")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable String contactId, Principal principal) {
        List<MessageResponse> messages = messageService.getMessages(contactId, principal.getName());
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId, Principal principal) {
        messageService.deleteMessage(messageId, principal.getName());
        return ResponseEntity.ok("Message deleted successfully");
    }

}

package org.example.message.controller;

import lombok.RequiredArgsConstructor;
import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.MessageResponse;
import org.example.message.dto.response.MessagesResponse;
import org.example.message.service.MessageService;
import org.springframework.http.ResponseEntity;
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
        if(principal==null){
            throw new RuntimeException("You are not authorized to send message");
        }
        MessageResponse response = messageService.createMessage(messageRequest, principal.getName());
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
    public ResponseEntity<?> getMessages(@PathVariable String contactId, Principal principal) {
        return ResponseEntity.ok(messageService.getMessages(contactId, principal.getName()));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId, Principal principal) {
        messageService.deleteMessage(messageId, principal.getName());
        return ResponseEntity.ok("Message deleted successfully");
    }

    @GetMapping("/chats")
    public ResponseEntity<?> getChats(Principal principal, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(messageService.getChats(principal.getName(), page, size));
    }

    @PostMapping("/markAsRead")
    public ResponseEntity<Void> markMessagesAsRead(@RequestBody List<String> messageIds, Principal principal) {
        messageService.markMessagesAsRead(messageIds, principal.getName());
        return ResponseEntity.ok().build();
    }


}

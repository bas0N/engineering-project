package org.example.message.controller;

import lombok.RequiredArgsConstructor;
import org.example.message.dto.MessageResponse;
import org.example.message.dto.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping( "/api/v1/message")
public class MessageController {

    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public ResponseEntity<Page<UserResponse>> getUserFromMessages() {
        return null;
    }

    @RequestMapping(path = "/{userId}/{productId}", method = RequestMethod.GET)
    public ResponseEntity<Page<MessageResponse>> getMessages(@PathVariable String userId, @PathVariable String productId) {
        return null;
    }

    @RequestMapping(path = "/{userId}", method = RequestMethod.POST)
    public ResponseEntity<MessageResponse> sendMessage(@PathVariable String userId) {
        return null;
    }

    @RequestMapping(path = "/{messageId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteMessage(@PathVariable String messageId) {
        return null;
    }

}

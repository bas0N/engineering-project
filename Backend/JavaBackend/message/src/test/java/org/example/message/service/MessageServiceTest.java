package org.example.message.service;

import jakarta.transaction.Transactional;
import org.example.commondto.UserDetailInfoEvent;
import org.example.message.dto.request.MessageRequest;
import org.example.message.dto.response.ChatResponse;
import org.example.message.dto.response.MessageResponse;
import org.example.message.dto.response.MessagesResponse;
import org.example.message.entity.Message;
import org.example.message.repository.MessageRepository;
import org.example.message.service.impl.MessageServiceImpl;
import org.example.message.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = {
        "spring.kafka.bootstrap-servers=",
        "spring.kafka.listener.auto-startup=false"
})
@ActiveProfiles("test")
@Transactional
public class MessageServiceTest {

    @Autowired
    private MessageServiceImpl messageService;

    @Autowired
    private MessageRepository messageRepository;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    @MockBean
    private UserServiceImpl userService;

    private String senderId;
    private String receiverId;

    @BeforeEach
    void setUp() {
        senderId = "sender-123";
        receiverId = "receiver-123";

    }

    @Test
    void testCreateMessage_Success() {
        // Arrange
        MessageRequest messageRequest = new MessageRequest("Hello, this is a test message", receiverId);

        // Act
        MessageResponse response = messageService.createMessage(messageRequest, senderId);

        // Assert
        assertNotNull(response);
        assertEquals(messageRequest.getContent(), response.getContent());
        assertEquals(senderId, response.getSenderId());
        assertEquals(receiverId, response.getReceiverId());
    }

    @Test
    void testGetMessages_Success() {
        // Arrange
        Message message1 = new Message();
        message1.setSenderId(senderId);
        message1.setReceiverId(receiverId);
        message1.setContent("Message 1");
        message1.setDateAdded(new Date()); // Set the dateAdded value
        messageRepository.saveAndFlush(message1);

        Message message2 = new Message();
        message2.setSenderId(senderId);
        message2.setReceiverId(receiverId);
        message2.setContent("Message 2");
        message2.setDateAdded(new Date()); // Set the dateAdded value
        messageRepository.saveAndFlush(message2);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", senderId);

        UserDetailInfoEvent senderInfo = new UserDetailInfoEvent();
        senderInfo.setUserId(senderId);
        senderInfo.setFirstName("John");
        senderInfo.setLastName("Doe");
        senderInfo.setEmail("john.doe@example.com");

        UserDetailInfoEvent receiverInfo = new UserDetailInfoEvent();
        receiverInfo.setUserId(receiverId);
        receiverInfo.setFirstName("Jane");
        receiverInfo.setLastName("Smith");
        receiverInfo.setEmail("jane.smith@example.com");

        doReturn(senderInfo).when(userService).getUserInfo(senderId);
        doReturn(receiverInfo).when(userService).getUserInfo(receiverId);

        // Act
        MessagesResponse response = messageService.getMessages(receiverId, request);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getMessages().size());
        assertEquals("John Doe", response.getSender().getUsername());
        assertEquals("Jane Smith", response.getReceiver().getUsername());
    }


    @Test
    void testGetChats_Success() {
        // Arrange
        Message message1 = new Message();
        message1.setSenderId(senderId);
        message1.setReceiverId(receiverId);
        message1.setContent("Hello");
        message1.setDateAdded(new Date()); // Set the dateAdded value
        messageRepository.saveAndFlush(message1);

        Message message2 = new Message();
        message2.setSenderId(receiverId);
        message2.setReceiverId(senderId);
        message2.setContent("Hi there!");
        message2.setDateAdded(new Date()); // Set the dateAdded value
        messageRepository.saveAndFlush(message2);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("userId", senderId);

        // Act
        List<ChatResponse> response = messageService.getChats(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
    }
}

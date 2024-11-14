package org.example.message.interceptor;

import org.example.message.config.WebSocketPrincipal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

public class UserChannelInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String userId = (String) accessor.getSessionAttributes().get("userId");
        if (userId != null) {
            accessor.setUser(new WebSocketPrincipal(userId));
        }
        return message;
    }
}

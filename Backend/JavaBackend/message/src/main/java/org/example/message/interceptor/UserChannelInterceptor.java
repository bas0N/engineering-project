package org.example.message.interceptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.jwtcommon.jwt.Utils;
import org.example.message.config.WebSocketPrincipal;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;

@Slf4j
@RequiredArgsConstructor
public class UserChannelInterceptor implements ChannelInterceptor {
    private final Utils utils;
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.debug("Otrzymano token JWT: {}", jwtToken);

            if (jwtToken != null && utils.validateToken(jwtToken)) {
                String userId = utils.getCurrentUserId(jwtToken);
                accessor.setUser(new WebSocketPrincipal(userId));
                log.info("Użytkownik uwierzytelniony: {}", userId);
            } else {
                log.warn("Nieprawidłowy token JWT");
                throw new IllegalArgumentException("Nieprawidłowy token JWT");
            }
        }
        return message;
    }
}

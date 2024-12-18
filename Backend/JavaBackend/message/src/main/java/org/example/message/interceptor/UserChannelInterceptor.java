package org.example.message.interceptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commonutils.Utils;
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
        log.info("Intercepting message: {}", accessor.getCommand());
        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand()) || StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");

            if (jwtToken != null && !jwtToken.isBlank() && jwtToken.startsWith("Bearer ")) {
                jwtToken = jwtToken.substring(7);
                log.info("Extracted JWT Token: {}", jwtToken);
                String userId = utils.extractUserIdFromToken(jwtToken);
                if (userId != null) {
                    accessor.setUser(new WebSocketPrincipal(userId));
                    log.info("WebSocket Principal set for UserId: {}", userId);
                } else {
                    log.warn("UserId is null after extracting from token. Possible invalid token.");
                }
            } else {
                log.warn("JWT Token is missing, invalid, or does not start with 'Bearer '");
            }
        }
        return message;
    }
}

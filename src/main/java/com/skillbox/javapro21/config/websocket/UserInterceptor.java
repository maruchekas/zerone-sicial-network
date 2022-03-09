package com.skillbox.javapro21.config.websocket;

import com.skillbox.javapro21.config.security.JwtGenerator;
import com.sun.security.auth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.ArrayList;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class UserInterceptor implements ChannelInterceptor {
    private final Logger logger = LoggerFactory.getLogger(UserInterceptor.class);
    private final JwtGenerator jwtGenerator;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        assert accessor != null;
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object headers = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            if (headers instanceof Map) {
                Object array = ((Map) headers).get("token");
                if (array instanceof ArrayList) {
                    String token = ((ArrayList<String>) array).get(0);
                    String username = jwtGenerator.getLoginFromToken(token);
                    accessor.setUser(new UserPrincipal(username));
                    logger.info(username + " connected");
                }
            }
        }
        return message;
    }
}
package com.yash.EduSmart.config;


import com.yash.EduSmart.dto.ChatMessage;
import com.yash.EduSmart.dto.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener
{
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketEventListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal principal = headerAccessor.getUser();
        if (principal!=null){
            var chatMessage = ChatMessage.builder().sender(principal.getName()).messageType(MessageType.LEAVE).build();
            messagingTemplate.convertAndSend("/queue/messages",chatMessage);
        }
    }
}

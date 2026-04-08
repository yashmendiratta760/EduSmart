package com.yash.EduSmart.config;

import com.yash.EduSmart.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class WebSocketJwtInterceptor implements ChannelInterceptor
{
    private final JWTUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    public WebSocketJwtInterceptor(
            JWTUtils jwtUtils,
            UserDetailsService userDetailsService
    ){
        this.jwtUtils=jwtUtils;
        this.userDetailsService=userDetailsService;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (headerAccessor==null) return message;

        if (StompCommand.CONNECT.equals(headerAccessor.getCommand())){
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
            if (authHeader!=null && authHeader.startsWith("Bearer ")){
                String jwt = authHeader.substring(7);
                String email = jwtUtils.extractEmail(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtils.validateToken(jwt)){
                    String userType = jwtUtils.extractUserType(jwt).toUpperCase();

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + userType))
                            );

                    headerAccessor.setUser(authenticationToken);
                }
            }
        }
        return message;
    }
}

package com.yash.EduSmart.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    String sender;
    String receiver;
    String message;
    MessageType messageType;
}

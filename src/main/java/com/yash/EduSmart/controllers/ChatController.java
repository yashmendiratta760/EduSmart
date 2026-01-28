package com.yash.EduSmart.controllers;

import com.yash.EduSmart.dto.Assignment;
import com.yash.EduSmart.dto.AssignmentDTO;
import com.yash.EduSmart.dto.ChatMessage;
import com.yash.EduSmart.service.AssignmentService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class ChatController
{

    @Autowired
    private AssignmentService assignmentService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(
            @Payload ChatMessage message,
            Principal principal
    ){
        log.error("Message hit");
        String sender = principal.getName();
        log.error(sender);
        log.error(message.getReceiver());

        message.setSender(sender);
        log.error(message.getMessage());

        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                message

        );

    }

    @MessageMapping("/group.sendMessage/{groupId}")
    @SendTo("/topic/group.receiveMessage/{groupId}")
    public ChatMessage sendGroupMessage(
            @DestinationVariable String groupId,
            ChatMessage message,
            Principal principal
    ){
        message.setSender(principal.getName());
        return message;
    }

    @MessageMapping("/assignment.send/{groupId}")
    @SendTo("/topic/assignment.receive/{groupId}")
    public Assignment sendAssignment(
            @DestinationVariable String groupId,
            Assignment assignment,
            Principal principal
    ){
        String[] parts = groupId.split("\\s+");

        String branch = parts[0];   // "CSE"
        System.out.println("BRANCH" + branch);
        String semester = parts[1]; // "3"

        assignment.setSender(principal.getName());
        assignment.setReceiver(groupId);
        Long id = assignmentService.createEntry(new AssignmentDTO(
                assignment.getTask(),
                assignment.getDeadline(),
                branch,
                semester
        ));
        assignment.setId(id);
        return assignment;
    }

}

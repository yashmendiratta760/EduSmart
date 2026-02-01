package com.yash.EduSmart.controllers;

import com.yash.EduSmart.Entity.ChatEntity;
import com.yash.EduSmart.dto.Assignment;
import com.yash.EduSmart.dto.AssignmentDTO;
import com.yash.EduSmart.dto.ChatMessage;
import com.yash.EduSmart.service.AssignmentService;
import com.yash.EduSmart.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
public class ChatController
{

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private ChatService chatService;

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }


    @MessageMapping("/chat.sendPrivateMessage")
    public void sendPrivateMessage(
            @Payload ChatMessage message,
            Principal principal
    ){
        try{
            if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
                return;
            }
            String receiver = safeTrim(message.getReceiver());
            String text = safeTrim(message.getMessage());
            if (receiver.isEmpty() || text.isEmpty()) return;

            String sender = principal.getName();

            message.setSender(sender);
            message.setReceiver(receiver);
            message.setMessage(text);
            long now = System.currentTimeMillis();

            ChatEntity entity = new ChatEntity();
            entity.setMsg(message.getMessage());
            entity.setSender(message.getSender());
            entity.setReceiver(message.getReceiver());
            entity.setIsSent(true);
            entity.setTimeStamp(now);

            chatService.addMessage(entity);



            // sends to /user/{receiver}/queue/messages
            messagingTemplate.convertAndSendToUser(receiver, "/queue/messages", message);

        } catch (Exception ignored) {

        }

    }

    @MessageMapping("/group.sendMessage/{groupId}")
    @SendTo("/topic/group.receiveMessage/{groupId}")
    public ChatMessage sendGroupMessage(@DestinationVariable String groupId,
                                        @Payload ChatMessage message,
                                        Principal principal) {
        try {
            if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
                return null;
            }
            if (message == null) return null;

            String gid = safeTrim(groupId);
            if (gid.isEmpty()) return null;

            String text = safeTrim(message.getMessage());
            if (text.isEmpty()) return null;

            message.setSender(principal.getName());
            message.setReceiver(gid);
            message.setMessage(text);

            ChatEntity entity = new ChatEntity();
            entity.setMsg(message.getMessage());
            entity.setSender(message.getSender());
            entity.setReceiver(groupId);
            entity.setTimeStamp(System.currentTimeMillis());
            entity.setIsSent(true);
            chatService.addMessage(entity);

            return message;

        } catch (Exception ignored) {
            return null;
        }
    }

    @MessageMapping("/assignment.send/{groupId}")
    @SendTo("/topic/assignment.receive/{groupId}")
    public Assignment sendAssignment(@DestinationVariable String groupId,
                                     @Payload Assignment assignment,
                                     Principal principal) {
        try {
            if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
                return null;
            }
            if (assignment == null) return null;

            String gid = safeTrim(groupId);
            if (gid.isEmpty()) return null;

            // Expecting "CSE 3" (branch + semester)
            String[] parts = gid.split("\\s+");
            if (parts.length < 2) return null;

            String branch = safeTrim(parts[0]);
            String semester = safeTrim(parts[1]);
            if (branch.isEmpty() || semester.isEmpty()) return null;

            // Validate assignment fields
            String task = safeTrim(assignment.getTask());
            if (task.isEmpty()) return null;

            // deadline could be String/Date/Long depending on your DTO
            // If it's a String, you can validate non-empty:
            Object deadlineObj = assignment.getDeadline();
            if (deadlineObj == null) return null;

            assignment.setSender(principal.getName());
            assignment.setReceiver(gid);

            Long id = assignmentService.createEntry(new AssignmentDTO(
                    task,
                    assignment.getDeadline(),
                    branch,
                    semester
            ));

            if (id == null) return null;

            assignment.setId(id);
            return assignment;

        } catch (Exception ignored) {
            return null;
        }
    }

}

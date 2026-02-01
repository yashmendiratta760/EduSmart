package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.ChatEntity;
import com.yash.EduSmart.repository.ChatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ChatService {

    @Autowired
    private ChatRepo chatRepo;

    public void addMessage(ChatEntity msg){
        chatRepo.save(msg);
    }

    public List<ChatEntity> getMessageBySender(String email,String receiver){
        return chatRepo.findBySenderAndReceiver(email,receiver);
    }
    public List<ChatEntity> getMessageByReceiver(String receiver){
        return chatRepo.findByReceiver(receiver);


    }

    public List<ChatEntity> getConversation(String a, String b) {
        return chatRepo.findConversation(a, b);
    }

}

package com.yash.EduSmart.controllers;

import com.yash.EduSmart.dto.AI.GeneralRequest;
import com.yash.EduSmart.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/AI")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat-general")
    public ResponseEntity<?> chat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody GeneralRequest request
    ) {

        String jwt = authHeader.substring(7);

        return ResponseEntity.ok(
                aiService.chatGeneral(jwt, request)
        );
    }
}

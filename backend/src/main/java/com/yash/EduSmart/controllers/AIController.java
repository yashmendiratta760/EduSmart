package com.yash.EduSmart.controllers;

import com.yash.EduSmart.config.SupabaseStorageClient;
import com.yash.EduSmart.dto.AI.ChatRequest;
import com.yash.EduSmart.dto.AI.GeneralRequest;
import com.yash.EduSmart.dto.PresignUploadRequest;
import com.yash.EduSmart.dto.PresignUploadResponse;
import com.yash.EduSmart.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/AI")
public class AIController {

    private final SupabaseStorageClient storageClient;

    @Autowired
    private AIService aiService;

    public AIController(SupabaseStorageClient storageClient) {
        this.storageClient = storageClient;
    }

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

    @PostMapping("/upload-doc")
    public ResponseEntity<?> uploadDoc(
            @RequestHeader("Authorization") String authHeader,
            Principal principal,
            @RequestBody PresignUploadRequest request
    ) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // build safe path
        String user = principal.getName(); // email or username
        String safeFileName = request.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
        String path = user + "/" + UUID.randomUUID() + "_" + safeFileName;

        Map<String, Object> supabaseResponse =
                storageClient.createSignedUrl(path);
        String url = (String) supabaseResponse.get("url");
        if (url == null) {
            throw new RuntimeException("Supabase did not return url: " + supabaseResponse);
        }



        String token = authHeader.replace("Bearer ", "");

        String response =
                aiService.uploadDocument(url, token);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/rag")
    public ResponseEntity<?> chat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChatRequest request
    ) {

        String jwt = authHeader.substring(7);

        return ResponseEntity.ok(
                aiService.rag(request,jwt)
        );
    }





}

package com.yash.EduSmart.controllers;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.config.SupabaseStorageClient;
import com.yash.EduSmart.dto.AI.ChatRequest;
import com.yash.EduSmart.dto.AI.GeneralRequest;
import com.yash.EduSmart.dto.AssignmentStudent;
import com.yash.EduSmart.dto.PresignUploadRequest;
import com.yash.EduSmart.dto.PresignUploadResponse;
import com.yash.EduSmart.service.*;
import com.yash.EduSmart.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/AI")
public class AIController {

    private final SupabaseStorageClient storageClient;

    @Autowired
    private AIService aiService;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private UserService userService;

    public AIController(SupabaseStorageClient storageClient) {
        this.storageClient = storageClient;
    }

    @PostMapping("/chat-general")
    public ResponseEntity<?> chat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) GeneralRequest request
    ) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body("Request body is required");
            }

            String token = extractToken(authHeader);

            Object result = aiService.chatGeneral(token, request);
            return ResponseEntity.ok(result);

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error in /chat-general: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while processing chat request");
        }
    }

    @PostMapping("/upload-doc")
    public ResponseEntity<?> uploadDoc(
            @RequestHeader("Authorization") String authHeader,
            Principal principal,
            @RequestBody(required = false) PresignUploadRequest request
    ) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            if (request == null || request.getFileName() == null || request.getFileName().isBlank()) {
                return ResponseEntity.badRequest().body("fileName is required");
            }

            String user = principal.getName(); // email or username
            String safeFileName = request.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
            String path = user + "/" + UUID.randomUUID() + "_" + safeFileName;

            System.out.println("Generated path = " + path);

            Map<String, Object> supabaseResponse = storageClient.createSignedUrl(path);

            if (supabaseResponse == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("No response from storage service");
            }

            System.out.println("Supabase response = " + supabaseResponse);

            String url = (String) supabaseResponse.get("url");
            if (url == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Storage service did not return a URL");
            }

            return ResponseEntity.ok(new PresignUploadResponse(path, url));

        } catch (Exception e) {
            System.out.println("Error in /upload-doc: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while generating upload URL");
        }
    }

    @PostMapping("/create-vector")
    public ResponseEntity<?> vector(
            @RequestBody(required = false) String file_url,
            @RequestHeader("Authorization") String authHeader
    ) {
        try {
            if (file_url == null || file_url.isBlank()) {
                return ResponseEntity.badRequest().body("file_url is required");
            }

            String token = extractToken(authHeader);

            return ResponseEntity.ok(aiService.uploadDocument(file_url, token));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error in /create-vector: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while creating vector");
        }
    }

    @PostMapping("/rag")
    public ResponseEntity<?> rag(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) ChatRequest request
    ) {
        try {
            if (request == null) {
                return ResponseEntity.badRequest().body("Request body is required");
            }

            String token = extractToken(authHeader);

            return ResponseEntity.ok(aiService.rag(request, token));

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error in /rag: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while processing RAG request");
        }
    }

    @PostMapping("/plan")
    public ResponseEntity<?> planSuggest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody(required = false) String query
    ) {
        try {
            if (query == null || query.isBlank()) {
                return ResponseEntity.badRequest().body("Query cannot be empty");
            }

            String token = extractToken(authHeader);

            UserEntity user = getAuthenticatedUser(token);

            Branch branch = user.getBranch();

            if (branch == null) {
                return ResponseEntity.badRequest().body("User has no branch assigned");
            }

            String timetable = "";
            try {
                var entries = timeTableService.getEntryByBranchAndSemester(
                        branch.getName(),
                        branch.getSemester()
                );
                if (entries != null) {
                    timetable = entries.stream()
                            .filter(java.util.Objects::nonNull)
                            .map(Object::toString)
                            .collect(Collectors.joining(", "));
                }
            } catch (Exception e) {
                System.out.println("Error fetching timetable: " + e.getMessage());
            }

            String holidays = "";
            try {
                var holidayList = holidayService.getAll();
                if (holidayList != null) {
                    holidays = holidayList.stream()
                            .filter(h -> h != null && h.getDate() != null)
                            .map(h -> h.getDate().toString())
                            .collect(Collectors.joining(", "));
                }
            } catch (Exception e) {
                System.out.println("Error fetching holidays: " + e.getMessage());
            }

            String assignments = "";
            try {
                var assignmentList = assignmentService.findAllForStudent(
                        branch.getName(),
                        branch.getSemester()
                );
                if (assignmentList != null) {
                    assignments = assignmentList.stream()
                            .filter(it -> it != null && it.getDeadline() >= System.currentTimeMillis())
                            .map(AssignmentStudent::getAssignment)
                            .filter(java.util.Objects::nonNull)
                            .collect(Collectors.joining(", "));
                }
            } catch (Exception e) {
                System.out.println("Error fetching assignments: " + e.getMessage());
            }

            System.out.println("timetable: " + timetable);
            System.out.println("assignments: " + assignments);
            System.out.println("holidays: " + holidays);
            System.out.println("branch: " + branch.getName());

            return ResponseEntity.ok(
                    aiService.plan(
                            query,
                            token,
                            timetable,
                            assignments,
                            holidays,
                            branch.getName()
                    )
            );

        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error in /plan: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Something went wrong while generating plan");
        }
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Invalid authorization header");
        }

        String token = authHeader.substring(7).trim();

        if (token.isEmpty()) {
            throw new SecurityException("Invalid authorization header");
        }

        return token;
    }

    private UserEntity getAuthenticatedUser(String token) {

        String email = jwtUtils.extractEmail(token);

        if (email == null || email.isBlank()) {
            throw new SecurityException("Invalid token");
        }

        UserEntity user = userService.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return user;
    }
}
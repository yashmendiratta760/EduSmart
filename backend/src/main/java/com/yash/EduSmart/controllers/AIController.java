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
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
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
            @RequestBody GeneralRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        String token = extractToken(authHeader);

        return ResponseEntity.ok(
                aiService.chatGeneral(token, request)
        );
    }

    @PostMapping("/upload-doc")
    public ResponseEntity<PresignUploadResponse> uploadDoc(
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

        System.out.println("Generated path = " + path);

        Map<String, Object> supabaseResponse =
                storageClient.createSignedUrl(path);

        System.out.println("Supabase response = " + supabaseResponse);
        String url = (String) supabaseResponse.get("url");
        if (url == null) {
            throw new RuntimeException("Supabase did not return url: " + supabaseResponse);
        }

        return ResponseEntity.ok(new PresignUploadResponse(path, url));
    }

    @PostMapping("/create-vector")
    public ResponseEntity<?> vector(@RequestBody String file_url ,
                                    @RequestHeader("Authorization") String authHeader){
        String token = extractToken(authHeader);

        return ResponseEntity.ok(aiService.uploadDocument(file_url,token));
    }


    @PostMapping("/rag")
    public ResponseEntity<?> rag(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChatRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        String token = extractToken(authHeader);

        return ResponseEntity.ok(
                aiService.rag(request, token)
        );
    }

    @PostMapping("/plan")
    public ResponseEntity<?> planSuggest(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody String query
    ) {

        if (query == null || query.isBlank()) {
            throw new IllegalArgumentException("Query cannot be empty");
        }

        String token = extractToken(authHeader);

        UserEntity user = getAuthenticatedUser(token);

        Branch branch = user.getBranch();

        if (branch == null) {
            throw new IllegalArgumentException("User has no branch assigned");
        }

        String timetable = timeTableService
                .getEntryByBranchAndSemester(
                        branch.getName(),
                        branch.getSemester()
                )
                .stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        String holidays = holidayService.getAll()
                .stream()
                .map(h -> h.getDate().toString())
                .collect(Collectors.joining(", "));

        String assignments = assignmentService
                .findAllForStudent(
                        branch.getName(),
                        branch.getSemester()
                )
                .stream()
                .filter(it -> it.getDeadline() >= System.currentTimeMillis())
                .map(AssignmentStudent::getAssignment)
                .collect(Collectors.joining(", "));

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
    }


    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityException("Invalid authorization header");
        }

        return authHeader.substring(7);
    }

    private UserEntity getAuthenticatedUser(String token) {

        String email = jwtUtils.extractEmail(token);

        UserEntity user = userService.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        return user;
    }





}

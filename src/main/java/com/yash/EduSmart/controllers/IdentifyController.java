package com.yash.EduSmart.controllers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.TokenData;
import com.yash.EduSmart.dto.UserDTO;
import com.yash.EduSmart.service.UserService;
import com.yash.EduSmart.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/users")
public class IdentifyController {

    // (Not used in the shown code, kept as-is)
    private final Map<String, Object> tempUserData = new ConcurrentHashMap<>();

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${google.client_id:}")
    private String clientId;

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        return ResponseEntity.ok("Checking...");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenData> login(@RequestBody(required = false) UserDTO userDTO) {
        try {
            if (userDTO == null) {
                return unauthorized("Request body missing");
            }

            String email = safeTrim(userDTO.getEmail());
            String password = safeTrim(userDTO.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                return unauthorized("Email/password required");
            }

            UserEntity user = userService.findByEmail(email);
            if (user == null) {
                return unauthorized("Invalid email or password");
            }

            String dbPassword = user.getPassword();
            if (dbPassword == null || dbPassword.isBlank()) {
                return unauthorized("Invalid email or password");
            }

            if (!passwordEncoder.matches(password, dbPassword)) {
                return unauthorized("Invalid email or password");
            }

            String userType = safeTrim(user.getUserType());
            if (userType.isEmpty()) userType = null;

            String token = jwtUtils.generateToken(email, userType);

            String branch = null;
            String sem = null;
            String enroll = null;

            if ("STUDENT".equalsIgnoreCase(userType) && user.getBranch() != null) {
                if (user.getBranch().getName() != null) branch = user.getBranch().getName();
                sem = String.valueOf(user.getBranch().getSemester());
                enroll = safeTrim(user.getEnroll());
                if (enroll.isEmpty()) enroll = null;
            }

            String name = safeTrim(user.getName());
            if (name.isEmpty()) name = null;

            TokenData tokenData = new TokenData(
                    email,
                    token,
                    "Verified successfully",
                    userType,
                    branch,
                    sem,
                    name,
                    enroll
            );

            return ResponseEntity.ok(tokenData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(fail("Something went wrong"));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<TokenData> verifyGoogleToken(@RequestBody(required = false) Map<String, String> body) {
        try {
            if (body == null) return unauthorized("Request body missing");

            String idTokenString = safeTrim(body.get("idToken"));
            if (idTokenString.isEmpty()) {
                return unauthorized("idToken missing");
            }

            String cid = safeTrim(clientId);
            if (cid.isEmpty()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(fail("Google client_id not configured"));
            }

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance()
            ).setAudience(Collections.singletonList(cid)).build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return unauthorized("Invalid token");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            if (payload == null) {
                return unauthorized("Invalid token payload");
            }

            String email = safeTrim(payload.getEmail());
            if (email.isEmpty()) {
                return unauthorized("Email not found in token");
            }

            UserEntity user = userService.findByEmail(email);

            // Auto-create if not exists
            if (user == null) {
                String rawPassword = UUID.randomUUID().toString();
                String encoded = passwordEncoder.encode(rawPassword);

                UserDTO newUser = new UserDTO();
                newUser.setEmail(email);
                newUser.setPassword(encoded);

                userService.createEntry(newUser);

                // Fetch again so userType/name/branch are populated (depending on your createEntry logic)
                user = userService.findByEmail(email);
                if (user == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(fail("User creation failed"));
                }
            }

            String userType = safeTrim(user.getUserType());
            if (userType.isEmpty()) userType = null;

            String token = jwtUtils.generateToken(email, userType);

            String branch = null;
            String sem = null;
            String enroll = null;

            if ("STUDENT".equalsIgnoreCase(userType) && user.getBranch() != null) {
                if (user.getBranch().getName() != null) branch = user.getBranch().getName();
                sem = String.valueOf(user.getBranch().getSemester());
                enroll = safeTrim(user.getEnroll());
                if (enroll.isEmpty()) enroll = null;
            }

            String name = safeTrim(user.getName());
            if (name.isEmpty()) name = null;

            TokenData tokenData = new TokenData(
                    email,
                    token,
                    "Verified successfully",
                    userType,
                    branch,
                    sem,
                    name,
                    enroll
            );

            return ResponseEntity.ok(tokenData);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(fail("Something went wrong"));
        }
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private ResponseEntity<TokenData> unauthorized(String msg) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(fail(msg));
    }

    private TokenData fail(String msg) {
        // keep your structure similar to what you had
        return new TokenData(null, null, msg, null, "", "0", "", "");
    }
}

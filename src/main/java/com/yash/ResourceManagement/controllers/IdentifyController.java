package com.yash.ResourceManagement.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.yash.ResourceManagement.Entity.UserEntity;
import com.yash.ResourceManagement.dto.OtpData;
import com.yash.ResourceManagement.dto.TempUserData;
import com.yash.ResourceManagement.dto.TokenData;
import com.yash.ResourceManagement.dto.UserDTO;
import com.yash.ResourceManagement.service.UserService;
import com.yash.ResourceManagement.utils.JWTUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class IdentifyController
{

    @Autowired
    private EmailController emailController;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${google.client_id}")
    private String ClientId;

    private final Map<String, TempUserData> tempUserData = new ConcurrentHashMap<>();

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody UserDTO user)
    {
        String email = user.getEmail();
        if(userService.existsByEmail(email))
        {
            log.info("Email already exist in database (Send-otp)");
            return ResponseEntity.status(409).body("User already exists");
        }
        String encodedPass = passwordEncoder.encode(user.getPassword());

        String otp = emailController.sendOtp(email);
        tempUserData.put(email,new TempUserData(encodedPass,otp));
        log.info("Your otp is "+otp);

        return ResponseEntity.ok("Otp sent Successfully");
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenData> signupVerify(@RequestBody OtpData otpData)
    {

        String email = otpData.getEmail();
        TempUserData tempData = tempUserData.get(email);
        if (tempData == null) {
            TokenData temp = new TokenData(null,null,"Please send otp first");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(temp);
        }
        if(tempData.getOtp().equals(otpData.getOtp()))
        {

            UserDTO user = new UserDTO();
            user.setEmail(email);
            user.setPassword(tempData.getPassword());
            boolean created = userService.createEntry(user);
            if(created) {
                String token = jwtUtils.generateToken(email);
                TokenData tokenData = new TokenData(email,token,"User Verified");
                tempUserData.remove(email);

                return ResponseEntity.ok(tokenData);
            }
            TokenData temp = new TokenData(null,null,"User not created");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(temp);
        }
        TokenData temp = new TokenData(null,null,"Enter correct otp");

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(temp);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenData> login(@RequestBody UserDTO userDTO)
    {
        String email = userDTO.getEmail();
        UserEntity user = userService.findByEmail(email);

        if(user!=null && passwordEncoder.matches(userDTO.getPassword(), user.getPassword()))
        {
            String token = jwtUtils.generateToken(email);
            TokenData tokenData = new TokenData(email,token,"Verified successfully");
            return ResponseEntity.ok(tokenData);
        }
        TokenData temp = new TokenData(null,null,"Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(temp);

    }




    @PostMapping("/google")
    public ResponseEntity<TokenData> verifyGoogleToken(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");




        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(ClientId))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                        new TokenData(null,null,"Token error"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            UserEntity user = userService.findByEmail(email);
            if(user==null)
            {
                user = new UserEntity();
                user.setEmail(email);
                user.setPassword(UUID.randomUUID().toString());

                UserDTO userDTO = new UserDTO();
                userDTO.setEmail(user.getEmail());
                userDTO.setPassword(user.getPassword());

                userService.createEntry(userDTO);



            }

            String token = jwtUtils.generateToken(user.getEmail());

            TokenData tokenData = new TokenData(email,token,"Token sent successfully");


            return ResponseEntity.ok(tokenData);



        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new TokenData(null,null,"Token verification failed"));
        }
    }





}

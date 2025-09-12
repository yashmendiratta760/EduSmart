package com.yash.ResourceManagement.controllers;


import com.yash.ResourceManagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.format.ResolverStyle;

@Component
public class EmailController
{
    @Autowired
    private EmailService emailService;

    private static final SecureRandom random = new SecureRandom();

    public String sendOtp(String to)
    {
        try {
            String otp = generateOtp();
            emailService.sendMail(to,
                    "You otp for authentication is : "+otp,
                    "Otp from xyz app");
            return otp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}

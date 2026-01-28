package com.yash.EduSmart.controllers;


import com.yash.EduSmart.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class EmailController {
    private static final SecureRandom random = new SecureRandom();
    @Autowired
    private EmailService emailService;

    public static String generateOtp() {
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public String sendOtp(String to) {
        try {
            String otp = generateOtp();
            emailService.sendMail(to,
                    "You otp for authentication is : " + otp,
                    "Otp from xyz app");
            return otp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}

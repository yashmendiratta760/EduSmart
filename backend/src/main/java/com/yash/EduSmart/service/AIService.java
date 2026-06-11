package com.yash.EduSmart.service;

import com.yash.EduSmart.dto.AI.ChatRequest;
import com.yash.EduSmart.dto.AI.GeneralRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class AIService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${AI_API_URL}")
    private String AI_API_URL;

    public String chatGeneral(String jwtToken, GeneralRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        String partEnd = "chat-general";
        HttpEntity<GeneralRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        AI_API_URL+partEnd,
                        HttpMethod.POST,
                        entity,
                        String.class
                );

        return response.getBody();
    }


    public String uploadDocument(String fileUrl, String jwtToken) {

        String apiUrl = AI_API_URL+ "upload";


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<String> request =
                new HttpEntity<>(fileUrl, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        request,
                        String.class
                );

        return response.getBody();
    }

    public String rag(ChatRequest request, String jwtToken) {

        String apiUrl = AI_API_URL+ "rag";


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<ChatRequest> request2 =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        request2,
                        String.class
                );

        return response.getBody();
    }


}



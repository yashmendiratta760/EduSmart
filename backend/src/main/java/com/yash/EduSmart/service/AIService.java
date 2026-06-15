package com.yash.EduSmart.service;

import com.yash.EduSmart.dto.AI.ChatRequest;
import com.yash.EduSmart.dto.AI.GeneralRequest;
import com.yash.EduSmart.dto.AI.PlannerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

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

        Map<String, String> body = Map.of(
                "file_url", fileUrl
        );


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<Map<String, String>> entity =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        apiUrl,
                        HttpMethod.POST,
                        entity,
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

    public String plan(String query,String jwtToken,String timeTable,String test,String holidays,String careerGoal){
        String url = AI_API_URL+"planner";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        PlannerRequest request = new PlannerRequest();
        request.setUser_query(query);
        request.setTests(test);
        request.setTimetable(timeTable);
        request.setHolidays(holidays);
        request.setCareer_goal(careerGoal);

        HttpEntity<PlannerRequest> request2 =
                new HttpEntity<>(request, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        request2,
                        String.class
                );

        return response.getBody();



    }


}



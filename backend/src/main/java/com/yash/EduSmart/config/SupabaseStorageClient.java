package com.yash.EduSmart.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class SupabaseStorageClient
{

    private final WebClient webClient;
    @Value("${supabase.bucket}")
    private String bucket;

    public SupabaseStorageClient(
            @Value("${SUPABASE_URL}") String supabaseUrl,
            @Value("${SUPABASE_SERVICE_KEY}") String serviceKey)
    {
        System.out.println("SUPABASE key prefix = " + serviceKey.substring(0, 20));

        this.webClient = WebClient.builder()
                .baseUrl(supabaseUrl)
                .defaultHeader("apikey",serviceKey)
                .defaultHeader("Authorization","Bearer "+serviceKey)
                .build();
    }

    public Map<String,Object> createSignedUrl(String path){
        Map<String, Object> resp = webClient.post()
                .uri("/storage/v1/object/upload/sign/{bucket}/{path}", bucket, path)
                .header("Content-Type","application/json")
                .bodyValue(Map.of("expiresIn",3600))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        System.out.println("RAW_SUPABASE_RESPONSE = " + resp);
        return resp;
    }


    public Map<String, Object> createSignedDownloadUrl(String path) {
        return webClient.post()
                .uri("/storage/v1/object/sign/" + bucket + "/" + path)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of("expiresIn", 600))
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }



}

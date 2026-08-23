package com.yash.EduSmart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService
{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void set(String key, Object value, long expirySeconds) {
        try {
            String json = objectMapper.writeValueAsString(value);

            redisTemplate.opsForValue()
                    .set(key, json, expirySeconds, TimeUnit.SECONDS);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing object", e);
        }
    }

    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(value.toString(), clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing object", e);
        }
    }

    public <T> T get(String key, TypeReference<T> typeReference) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(
                    value.toString(),
                    typeReference
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

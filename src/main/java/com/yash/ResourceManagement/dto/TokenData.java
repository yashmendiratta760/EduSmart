package com.yash.ResourceManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenData {
    String email;
    String token;
    String message;


    public TokenData(String email,String token,String message) {
        this.message = message;
        this.token = token;
        this.email = email;
    }
}

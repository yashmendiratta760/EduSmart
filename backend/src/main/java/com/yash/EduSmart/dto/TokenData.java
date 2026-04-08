package com.yash.EduSmart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenData {
    String email;
    String token;
    String message;
    String userType;
    String branch;
    String semester;
    String name;
    String enroll;
}

package com.yash.EduSmart.dto.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserDto
{
    private String email;
    private String pass;
    private String user_type;
    private String name;
    private String enroll;
    private String branch;
    private String semester;
}

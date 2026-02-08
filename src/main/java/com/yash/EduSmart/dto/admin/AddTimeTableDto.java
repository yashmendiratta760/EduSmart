package com.yash.EduSmart.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddTimeTableDto
{
    private String day;
    private String subject;
    private String startTime;
    private String endTime;
    private String branch;
    private String sem;
    private String teacherEmail;
    private String roomNo;
}

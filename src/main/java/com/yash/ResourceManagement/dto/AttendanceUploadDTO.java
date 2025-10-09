package com.yash.ResourceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUploadDTO {
    List<String> studentEmails;
    String subjectName;
    String day;
    String time;
    String branch;
    int semester;
    String status;
}

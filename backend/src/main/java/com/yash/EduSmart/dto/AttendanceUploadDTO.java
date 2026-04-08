package com.yash.EduSmart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceUploadDTO {
    List<AttendanceStatus> attendance;
    String subjectName;
    String time;
    String branch;
    int semester;
    LocalDate date;
}

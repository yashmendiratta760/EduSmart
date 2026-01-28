package com.yash.EduSmart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AssignmentStudent {
    Long id;
    String branch;
    String sem;
    String assignment;
    Long deadline;
}

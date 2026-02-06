package com.yash.EduSmart.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AssignmentGetDTO
{
    Long id;
    String assignment;
    Long deadline;
    List<String> enroll;
    String branch;
    String sem;
    String path;
}

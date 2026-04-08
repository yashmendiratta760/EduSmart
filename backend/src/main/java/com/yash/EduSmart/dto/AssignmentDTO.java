package com.yash.EduSmart.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignmentDTO {
    String assignment;
    Long deadline;
    String branch;
    String semester;
    String path;
}

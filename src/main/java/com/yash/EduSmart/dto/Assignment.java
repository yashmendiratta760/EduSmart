package com.yash.EduSmart.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assignment
{
    Long id;
    String sender;
    String receiver;
    String task;
    String path;
    Long deadline;
}

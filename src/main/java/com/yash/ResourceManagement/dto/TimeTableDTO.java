package com.yash.ResourceManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTableDTO
{
    String day;
    String subject;
    String time;
    String branch;
}

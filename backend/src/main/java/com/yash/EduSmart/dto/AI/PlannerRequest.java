package com.yash.EduSmart.dto.AI;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlannerRequest {
    String user_query;
    String current_date = java.time.LocalDate.now().toString();
    String tests = "";
    String assignments = "";
    String timetable = "";
    String holidays = "";
    String user_response = "";
    String career_goal="";
    String preferred_activities="";
    String available_hours="";
    List<String> history= Collections.emptyList();
}

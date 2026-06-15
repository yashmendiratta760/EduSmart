package com.yash.EduSmart.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralRequest{
    String user_query;
    String current_date = LocalDate.now().toString();
    List<String> history= Collections.emptyList();
}

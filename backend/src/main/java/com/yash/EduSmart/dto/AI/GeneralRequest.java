package com.yash.EduSmart.dto.AI;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralRequest{
    String user_query;
    String current_date;
    List<String> history= Collections.emptyList();
}

package com.yash.EduSmart.dto.AI;

import lombok.*;

import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRequest
{
    String query;
    List<String> history = Collections.emptyList();
}

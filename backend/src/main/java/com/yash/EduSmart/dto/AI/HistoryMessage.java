package com.yash.EduSmart.dto.AI;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HistoryMessage {
    String role;
    String content;
}

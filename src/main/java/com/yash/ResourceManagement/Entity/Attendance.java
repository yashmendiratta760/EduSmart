package com.yash.ResourceManagement.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity student;

    @ManyToOne
    @JoinColumn(name = "timetable_id")
    private TimeTableEntry timeTable;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDate date;
}

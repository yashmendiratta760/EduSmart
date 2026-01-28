package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Attendance;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent_Id(Long userId);

    Attendance findByStudentAndTimeTableAndDate(UserEntity student, TimeTableEntry timeTableEntry, LocalDate date);
}
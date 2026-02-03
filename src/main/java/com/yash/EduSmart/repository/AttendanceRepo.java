package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Attendance;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepo extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent_Id(Long userId);

    Attendance findByStudentAndTimeTableAndDate(UserEntity student, TimeTableEntry timeTableEntry, LocalDate date);

    @Query("""
    SELECT a FROM Attendance a
    WHERE a.timeTable = :timeTableEntry
      AND a.date = :date
      AND a.student.email IN :emails
    """)
    List<Attendance> findAllForUpload(
            @Param("timeTableEntry") TimeTableEntry timeTableEntry,
            @Param("date") LocalDate date,
            @Param("emails") List<String> emails
    );


}
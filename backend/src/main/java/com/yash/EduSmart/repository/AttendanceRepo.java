package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Attendance;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.AttendanceDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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


    @Query("""
    select new com.yash.EduSmart.dto.AttendanceDTO(a.date, a.status, t.subject)
    from Attendance a
    join a.timeTable t
    where a.student.id = :userId
    order by a.date desc
""")
    List<AttendanceDTO> findAttendanceDtoByStudentId(@Param("userId") Long userId);

    @Modifying
    @Query(value = "TRUNCATE TABLE attendance RESTART IDENTITY CASCADE", nativeQuery = true)
    void truncateAttendance();



}
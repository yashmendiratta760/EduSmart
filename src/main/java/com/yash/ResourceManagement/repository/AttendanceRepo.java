package com.yash.ResourceManagement.repository;

import com.yash.ResourceManagement.Entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepo extends JpaRepository<Attendance,Long> {
    List<Attendance> findByStudent_Id(Long userId);
}
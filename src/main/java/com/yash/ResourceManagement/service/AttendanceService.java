package com.yash.ResourceManagement.service;

import com.yash.ResourceManagement.Entity.Attendance;
import com.yash.ResourceManagement.repository.AttendanceRepo;
import org.springframework.beans.factory.annotation.Autowired;

public class AttendanceService
{

    @Autowired
    private AttendanceRepo attendanceRepo;

    public void getAttendance(Long userId){
        Attendance userAttendance = attendanceRepo.getby
    }
}

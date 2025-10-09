package com.yash.ResourceManagement.service;

import com.yash.ResourceManagement.Entity.Attendance;
import com.yash.ResourceManagement.Entity.TimeTableEntry;
import com.yash.ResourceManagement.Entity.UserEntity;
import com.yash.ResourceManagement.dto.AttendanceDTO;
import com.yash.ResourceManagement.repository.AttendanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceService
{

    @Autowired
    private AttendanceRepo attendanceRepo;

    public List<AttendanceDTO> getAttendance(Long userId){
        List<Attendance> userAttendance = attendanceRepo.findByStudent_Id(userId);

        List<AttendanceDTO> attendanceDTO = new ArrayList<>();
        userAttendance.forEach(attendance -> {
            attendanceDTO.add(new AttendanceDTO(attendance.getDate(),attendance.getStatus(),attendance.getTimeTable().getSubject()));
        });
        return attendanceDTO;
    }

    public void createEntry(TimeTableEntry timeTableEntry, String status, UserEntity student, LocalDate date){
        Attendance attendanceEntity = new Attendance();
        attendanceEntity.setDate(date);
        attendanceEntity.setTimeTable(timeTableEntry);
        attendanceEntity.setStatus(status);
        attendanceEntity.setStudent(student);
        attendanceRepo.save(attendanceEntity);
    }
}

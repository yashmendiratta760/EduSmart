package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.Attendance;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.AttendanceDTO;
import com.yash.EduSmart.repository.AttendanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepo attendanceRepo;

    public List<AttendanceDTO> getAttendance(Long userId) {
        List<Attendance> userAttendance = attendanceRepo.findByStudent_Id(userId);

        List<AttendanceDTO> attendanceDTO = new ArrayList<>();
        userAttendance.forEach(attendance -> {
            attendanceDTO.add(new AttendanceDTO(attendance.getDate(), attendance.getStatus(), attendance.getTimeTable().getSubject()));
        });
        return attendanceDTO;
    }

    public Attendance findEntry(UserEntity student, TimeTableEntry timeTable, LocalDate date) {
        Attendance entity = attendanceRepo.findByStudentAndTimeTableAndDate(student, timeTable, date);
        if (entity != null) return entity;
        else return null;
    }

    public void createEntry(TimeTableEntry timeTableEntry, String status, UserEntity student, LocalDate date) {
        Attendance attendanceEntity = new Attendance();
        attendanceEntity.setDate(date);
        attendanceEntity.setTimeTable(timeTableEntry);
        attendanceEntity.setStatus(status);
        attendanceEntity.setStudent(student);
        attendanceRepo.save(attendanceEntity);
    }

    public void updateEntry(Attendance entity, String status) {
        entity.setStatus(status);
        attendanceRepo.save(entity);
    }

    public List<Attendance> getAllEntry(TimeTableEntry timeTableEntry,LocalDate date,List<String> emails){
        return attendanceRepo.findAllForUpload(timeTableEntry,date,emails);
    }

    public void saveAll(List<Attendance> attendances){
        attendanceRepo.saveAll(attendances);
    }
}

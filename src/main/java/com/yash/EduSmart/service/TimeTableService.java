package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.TeacherDTO;
import com.yash.EduSmart.dto.TimeTableDTO;
import com.yash.EduSmart.dto.admin.AddBranchDto;
import com.yash.EduSmart.dto.admin.AddTimeTableDto;
import com.yash.EduSmart.repository.AttendanceRepo;
import com.yash.EduSmart.repository.BranchRepo;
import com.yash.EduSmart.repository.TimeTableRepo;
import com.yash.EduSmart.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class TimeTableService {
    @Autowired
    private TimeTableRepo timeTableRepo;

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepo attendanceRepo;

    public List<TimeTableDTO> findByTeacherId(Long id){
        return timeTableRepo.getTeacherTimeTable(id);
    }


    public List<TimeTableDTO> getEntryByBranchAndSemester(String branch, int semester) {
        List<TimeTableDTO> timeTableEntries = timeTableRepo.findByBranchAndSemester(branch,semester);
        return timeTableEntries;
    }

    public TimeTableEntry getAttendanceUploadSlot(String day, String subject, Branch branch, String time) {
        return timeTableRepo.findByDayAndSubjectAndBranchAndTiming(day, subject, branch, time);
    }

    public List<String> getAllSubjects(String branch, int semester) {
        return timeTableRepo.findDistinctSubjects(branch, semester);

    }

    public List<TeacherDTO> findTeachersByBranchAndSemDto(String branch, String sem) {
        return timeTableRepo.findTeacherDtos(branch, Integer.parseInt(sem));
    }

    public void createTimeTableEntries(List<AddTimeTableDto> timeTableDtos){
        Branch branch = branchRepo.findByNameAndSemester(timeTableDtos.getFirst().getBranch(), Integer.parseInt(timeTableDtos.getFirst().getSem()));
        List<TimeTableEntry> entries = timeTableDtos.stream()
                .map(dto -> {
                    UserEntity user = userRepository.findByEmail(dto.getTeacherEmail());
                    TimeTableEntry entry = new TimeTableEntry();
                    entry.setDay(dto.getDay());
                    entry.setSubject(dto.getSubject());
                    entry.setTiming(dto.getStartTime()+"-"+dto.getEndTime());
                    entry.setBranch(branch);   // or fetch from repo if needed
                    entry.setTeacher(user); // same here
                    entry.setRoom(dto.getRoomNo());
                    return entry;
                })
                .toList();

        timeTableRepo.saveAll(entries);
    }

    public  void deleteAll(){
        timeTableRepo.deleteAll();
    }





}

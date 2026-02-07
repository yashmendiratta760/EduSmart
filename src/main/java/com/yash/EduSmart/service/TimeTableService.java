package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.TeacherDTO;
import com.yash.EduSmart.dto.TimeTableDTO;
import com.yash.EduSmart.repository.BranchRepo;
import com.yash.EduSmart.repository.TimeTableRepo;
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



}

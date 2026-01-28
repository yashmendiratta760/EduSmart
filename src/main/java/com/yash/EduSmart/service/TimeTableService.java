package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.dto.TimeTableDTO;
import com.yash.EduSmart.repository.BranchRepo;
import com.yash.EduSmart.repository.TimeTableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeTableService {
    @Autowired
    private TimeTableRepo timeTableRepo;

    @Autowired
    private BranchRepo branchRepo;

    public void createEntry(TimeTableDTO timeTableDTO) {
        TimeTableEntry timeTableEntry = new TimeTableEntry();
        timeTableEntry.setDay(timeTableDTO.getDay());
        timeTableEntry.setSubject(timeTableDTO.getSubject());
        timeTableEntry.setTiming(timeTableDTO.getTime());

        Branch branch = branchRepo.findByName(timeTableDTO.getBranch());

        timeTableEntry.setBranch(branch);

        timeTableRepo.save(timeTableEntry);
    }

    public List<TimeTableEntry> getEntryByBranchAndSemester(String branch, int semester) {
        List<TimeTableEntry> timeTableEntries = timeTableRepo.findByBranchName(branch);
        List<TimeTableEntry> filteredEntries = timeTableEntries
                .stream()
                .filter(it -> it.getBranch().getSemester() == semester)
                .toList();
        return filteredEntries;
    }

    public TimeTableEntry getAttendanceUploadSlot(String day, String subject, Branch branch, String time) {
        return timeTableRepo.findByDayAndSubjectAndBranchAndTiming(day, subject, branch, time);
    }

    public List<String> getAllSubjects(String branch, int semester) {
        List<String> subjects = getEntryByBranchAndSemester(branch, semester).stream().map(it ->
                it.getSubject()).toList();
        return subjects;
    }


}

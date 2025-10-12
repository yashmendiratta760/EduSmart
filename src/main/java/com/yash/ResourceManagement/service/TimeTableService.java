package com.yash.ResourceManagement.service;

import com.yash.ResourceManagement.Entity.Branch;
import com.yash.ResourceManagement.Entity.TimeTableEntry;
import com.yash.ResourceManagement.dto.TimeTableDTO;
import com.yash.ResourceManagement.repository.BranchRepo;
import com.yash.ResourceManagement.repository.TimeTableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.List;

@Service
public class TimeTableService
{
    @Autowired
    private TimeTableRepo timeTableRepo;

    @Autowired
    private BranchRepo branchRepo;

    public void createEntry(TimeTableDTO timeTableDTO)
    {
        TimeTableEntry timeTableEntry = new TimeTableEntry();
        timeTableEntry.setDay(timeTableDTO.getDay());
        timeTableEntry.setSubject(timeTableDTO.getSubject());
        timeTableEntry.setTiming(timeTableDTO.getTime());

        Branch branch = branchRepo.findByName(timeTableDTO.getBranch());

        timeTableEntry.setBranch(branch);

        timeTableRepo.save(timeTableEntry);
    }

    public List<TimeTableEntry> getEntryByBranchAndSemester(String branch,int semester)
    {
        List<TimeTableEntry> timeTableEntries =  timeTableRepo.findByBranchName(branch);
        List<TimeTableEntry> filteredEntries = timeTableEntries
                .stream()
                .filter(it -> it.getBranch().getSemester() == semester)
                .toList();
        return filteredEntries;
    }

    public TimeTableEntry getAttendanceUploadSlot(String day, String subject, Branch branch,String time){
        return timeTableRepo.findByDayAndSubjectAndBranchAndTiming(day, subject, branch,time);
    }


}

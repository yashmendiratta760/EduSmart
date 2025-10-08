package com.yash.ResourceManagement.service;

import com.yash.ResourceManagement.Entity.Branch;
import com.yash.ResourceManagement.Entity.TimeTableEntry;
import com.yash.ResourceManagement.dto.TimeTableDTO;
import com.yash.ResourceManagement.repository.BranchRepo;
import com.yash.ResourceManagement.repository.TimeTableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<TimeTableEntry> getEntryByBranch(String branch)
    {
        return timeTableRepo.findByBranchName(branch);
    }


}

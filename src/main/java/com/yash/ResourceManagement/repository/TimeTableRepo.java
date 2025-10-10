package com.yash.ResourceManagement.repository;

import com.yash.ResourceManagement.Entity.Branch;
import com.yash.ResourceManagement.Entity.TimeTableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeTableRepo extends JpaRepository<TimeTableEntry,Long>
{
    List<TimeTableEntry> findByBranchName(String branchName);
    TimeTableEntry findByDayAndSubjectAndBranchAndTiming(String day, String subject, Branch branch,String time);

}

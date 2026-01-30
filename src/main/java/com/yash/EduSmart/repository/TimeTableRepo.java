package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.TimeTableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimeTableRepo extends JpaRepository<TimeTableEntry, Long> {
    List<TimeTableEntry> findByBranchName(String branch);

    TimeTableEntry findByDayAndSubjectAndBranchAndTiming(String day, String subject, Branch branch, String time);

    List<TimeTableEntry> findByTeacherId(Long id);

}

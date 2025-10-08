package com.yash.ResourceManagement.controllers;

import com.yash.ResourceManagement.Entity.TimeTableEntry;
import com.yash.ResourceManagement.dto.TimeTableDTO;
import com.yash.ResourceManagement.service.BranchService;
import com.yash.ResourceManagement.service.TimeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController
{

    @Autowired
    private TimeTableService timeTableService;

    @GetMapping("/getTimeTableByDay")
    public ResponseEntity<List<TimeTableDTO>> getTimeTableByDays(
            @RequestParam String branch)
    {
        List<TimeTableEntry> timeTableEntries =  timeTableService.getEntryByBranch(branch);
        List<TimeTableDTO> timeTableDTOS = new ArrayList<>();
        for (int i = 0; i < timeTableEntries.size(); i++) {
            timeTableDTOS.add(new TimeTableDTO(
                    timeTableEntries.get(i).getDay(),
                    timeTableEntries.get(i).getSubject(),
                    timeTableEntries.get(i).getTiming(),
                    timeTableEntries.get(i).getBranch().getName()
            ));
        }


        return ResponseEntity.ok(timeTableDTOS);
    }

}

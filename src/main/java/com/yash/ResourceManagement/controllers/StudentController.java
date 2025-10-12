package com.yash.ResourceManagement.controllers;

import com.yash.ResourceManagement.Entity.Branch;
import com.yash.ResourceManagement.Entity.TimeTableEntry;
import com.yash.ResourceManagement.Entity.UserEntity;
import com.yash.ResourceManagement.dto.AttendanceDTO;
import com.yash.ResourceManagement.dto.AttendanceUploadDTO;
import com.yash.ResourceManagement.dto.TimeTableDTO;
import com.yash.ResourceManagement.service.AttendanceService;
import com.yash.ResourceManagement.service.BranchService;
import com.yash.ResourceManagement.service.TimeTableService;
import com.yash.ResourceManagement.service.UserService;
import org.checkerframework.checker.units.qual.A;
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

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/getTimeTableByDay")
    public ResponseEntity<List<TimeTableDTO>> getTimeTableByDays(
            @RequestParam String branch,
            @RequestParam String semester)
    {
        List<TimeTableEntry> timeTableEntries =  timeTableService.getEntryByBranchAndSemester(branch,Integer.parseInt(semester));

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

    @GetMapping("/getAttendance")
    public ResponseEntity<List<AttendanceDTO>> getAttendance(@RequestParam String email){
        Long studentId = userService.findByEmail(email).getId();
        List<AttendanceDTO> attendanceDTOList = attendanceService.getAttendance(studentId);
        return ResponseEntity.ok(attendanceDTOList);
    }



}

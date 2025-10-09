package com.yash.ResourceManagement.controllers;


import com.yash.ResourceManagement.Entity.Branch;
import com.yash.ResourceManagement.Entity.TimeTableEntry;
import com.yash.ResourceManagement.dto.AttendanceUploadDTO;
import com.yash.ResourceManagement.dto.BranchDTO;
import com.yash.ResourceManagement.dto.TimeTableDTO;
import com.yash.ResourceManagement.repository.BranchRepo;
import com.yash.ResourceManagement.repository.TimeTableRepo;
import com.yash.ResourceManagement.service.AttendanceService;
import com.yash.ResourceManagement.service.BranchService;
import com.yash.ResourceManagement.service.TimeTableService;
import com.yash.ResourceManagement.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/teacher")
public class TeacherController
{

    @Autowired
    private TimeTableService timeTableService;
    @Autowired
    private BranchService branchService;

    @Autowired
    private UserService userService;

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping("/time-table-upload")
    public ResponseEntity<String> timeTableEntry(@RequestBody TimeTableDTO timeTableDTO){
        try {
            timeTableService.createEntry(timeTableDTO);
            return ResponseEntity.ok("Entry Created");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error occurred");
        }
    }

    @PostMapping("/branch-add")
    public ResponseEntity<String> addBranch(@RequestBody BranchDTO branchDTO)
    {
        try {
            branchService.createBranch(branchDTO.getName());
            return ResponseEntity.ok("Branch added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Some error occurred in adding branch");
        }
    }

    @PostMapping("/uploadAttendance")
    public ResponseEntity<String> uploadAttendance(@RequestBody AttendanceUploadDTO uploadDTO){
        uploadDTO.getStudentEmails().forEach(student-> {
            Branch branch = branchService.getByNameAndSemester(uploadDTO.getBranch(), uploadDTO.getSemester());
            TimeTableEntry timeTableEntry =  timeTableService.getAttendanceUploadSlot(uploadDTO.getDay(),
                    uploadDTO.getSubjectName(),branch,
                    uploadDTO.getTime());

            attendanceService.createEntry(timeTableEntry,uploadDTO.getStatus(),
                    userService.findByEmail(student), LocalDate.now());
        });



        return ResponseEntity.ok("Uploaded");
    }
}

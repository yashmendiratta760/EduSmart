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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
@Slf4j
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
        int success = 0,fail = 0;
        for (String student : uploadDTO.getStudentEmails()){
            Branch branch = branchService.getByNameAndSemester(uploadDTO.getBranch(), uploadDTO.getSemester());
            TimeTableEntry timeTableEntry =  timeTableService.getAttendanceUploadSlot(uploadDTO.getDate().getDayOfWeek().name(),
                    uploadDTO.getSubjectName(),branch,
                    uploadDTO.getTime());

            if (timeTableEntry != null) {
                success++;
                attendanceService.createEntry(
                        timeTableEntry,
                        uploadDTO.getStatus(),
                        userService.findByEmail(student),
                        uploadDTO.getDate()
                );
            } else {
                fail++;
            }
        }
        if (success == 0) {
            return ResponseEntity
                    .badRequest()
                    .body("Attendance upload failed. No entries were created.");
        } else if (fail > 0) {
            return ResponseEntity
                    .ok("⚠Attendance partially uploaded. Success: " + success + ", Failed: " + fail);
        } else {
            return ResponseEntity
                    .ok("Attendance uploaded successfully");
        }
    }
}

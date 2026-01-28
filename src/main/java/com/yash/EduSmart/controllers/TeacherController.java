package com.yash.EduSmart.controllers;


import com.yash.EduSmart.Entity.*;
import com.yash.EduSmart.dto.*;
import com.yash.EduSmart.service.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TimeTableService timeTableService;
    @Autowired
    private BranchService branchService;

    @Autowired
    private UserService userService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AssignmentService assignmentService;

    @PostMapping("/time-table-upload")
    public ResponseEntity<String> timeTableEntry(@RequestBody TimeTableDTO timeTableDTO) {
        try {
            timeTableService.createEntry(timeTableDTO);
            return ResponseEntity.ok("Entry Created");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Error occurred");
        }
    }

    @PostMapping("/branch-add")
    public ResponseEntity<String> addBranch(@RequestBody BranchDTO branchDTO) {
        try {
            branchService.createBranch(branchDTO.getName());
            return ResponseEntity.ok("Branch added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(404).body("Some error occurred in adding branch");
        }
    }

    @PostMapping("/uploadAttendance")
    public ResponseEntity<String> uploadAttendance(@RequestBody AttendanceUploadDTO uploadDTO) {
        int success = 0, fail = 0;
        for (AttendanceStatus attendance : uploadDTO.getAttendance()) {
            Branch branch = branchService.getByNameAndSemester(uploadDTO.getBranch(), uploadDTO.getSemester());
            TimeTableEntry timeTableEntry = timeTableService.getAttendanceUploadSlot(uploadDTO.getDate().getDayOfWeek().name().toUpperCase(),
                    uploadDTO.getSubjectName(), branch,
                    uploadDTO.getTime());

            if (timeTableEntry != null) {
                UserEntity userEntity = userService.findByEmail(attendance.getEmail());
                if (userEntity != null) {
                    Attendance entity = attendanceService.findEntry(userEntity, timeTableEntry, uploadDTO.getDate());
                    if (entity != null) {
                        attendanceService.updateEntry(entity, attendance.getStatus());
                    } else {

                        attendanceService.createEntry(
                                timeTableEntry,
                                attendance.getStatus(),
                                userService.findByEmail(attendance.getEmail()),
                                uploadDTO.getDate()
                        );
                    }
                    success++;
                } else {
                    fail++;
                }
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

    @GetMapping("/getTimeTableByDay")
    public ResponseEntity<List<TimeTableDTO>> getTimeTableByDays(
            @RequestParam String branch,
            @RequestParam String semester) {
        log.error("HIT");
        List<TimeTableEntry> timeTableEntries = timeTableService.getEntryByBranchAndSemester(branch, Integer.parseInt(semester));

        List<TimeTableDTO> timeTableDTOS = new ArrayList<>();
        for (int i = 0; i < timeTableEntries.size(); i++) {
            timeTableDTOS.add(new TimeTableDTO(
                    timeTableEntries.get(i).getDay(),
                    timeTableEntries.get(i).getSubject(),
                    timeTableEntries.get(i).getTiming(),
                    timeTableEntries.get(i).getBranch().getName()
            ));
        }

        System.out.println(timeTableDTOS);


        return ResponseEntity.ok(timeTableDTOS);
    }

    @PostMapping("/getStudentsListTeacher")
    public ResponseEntity<List<StudentData>> getStudentsList(@RequestBody StudentsListDTO studentsListDTO) {

        log.info(String.valueOf(Integer.parseInt(studentsListDTO.getSemester())));
        List<UserEntity> studentsData = userService.findStudentsByBranch(studentsListDTO.getBranch(), Integer.parseInt(studentsListDTO.getSemester()));
        if (!studentsData.isEmpty()) {
            List<StudentData> studentEmails = studentsData.stream().map(it ->
                            new StudentData(it.getEmail(), it.getName()))
                    .toList();
            return ResponseEntity.ok(studentEmails);

        } else {
            return ResponseEntity.badRequest().body(new ArrayList<StudentData>());
        }

    }

    @GetMapping("/getAllBranch")
    public ResponseEntity<List<String>> getBranchList(){
        return ResponseEntity.ok(branchService.getAllBranch());
    }

    @GetMapping("/getAllAssignments")
    public ResponseEntity<List<AssignmentGetDTO>> getAll(){
        List<AssignmentEntity> assignmentEntities = assignmentService.getAll();
        List<AssignmentGetDTO> assignmentGetDTOS = assignmentEntities.stream()
                .map(it -> new AssignmentGetDTO(
                        it.getId(),
                        it.getAssignment(),
                        it.getDeadline(),
                        it.getCompletedUsers()
                                .stream()
                                .map(UserEntity::getEnroll)
                                .toList(),
                        it.getBranch().getName(),
                        String.valueOf(it.getBranch().getSemester())
                ))
                .toList();

        return ResponseEntity.ok(assignmentGetDTOS);

    }


}

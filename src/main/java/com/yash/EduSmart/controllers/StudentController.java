package com.yash.EduSmart.controllers;

import com.yash.EduSmart.Entity.AssignmentEntity;
import com.yash.EduSmart.Entity.HolidayEntity;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.*;
import com.yash.EduSmart.service.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/getTimeTableByDay")
    public ResponseEntity<List<TimeTableDTO>> getTimeTableByDays(
            @RequestParam String branch,
            @RequestParam String semester) {
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


        return ResponseEntity.ok(timeTableDTOS);
    }

    @PostMapping("/getStudentsList")
    public ResponseEntity<List<StudentData>> getStudentsList(@RequestBody StudentsListDTO studentsListDTO) {

//        log.info(String.valueOf(Integer.parseInt(studentsListDTO.getSemester())));
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

    @GetMapping("/getAllSubjects")
    public ResponseEntity<List<String>> getAllSubjects(@RequestParam String branch,
                                                       @RequestParam String semester) {
        try {
            if (Objects.equals(semester, "")) {
                return ResponseEntity.badRequest().body(new ArrayList<>(Collections.singleton("Some field is empty")));
            } else {
                List<String> subjects = timeTableService.getAllSubjects(branch, Integer.parseInt(semester));
                return ResponseEntity.ok(subjects);
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.internalServerError().body(new ArrayList<>(Collections.singleton("Some error occurred")));
        }
    }

    @GetMapping("/getAttendance")
    public ResponseEntity<List<AttendanceDTO>> getAttendance(@RequestParam String email) {
        System.out.println("HITTT");
        Long studentId = userService.findByEmail(email).getId();
        List<AttendanceDTO> attendanceDTOList = attendanceService.getAttendance(studentId);
        return ResponseEntity.ok(attendanceDTOList);
    }

    @PostMapping("/markAssignment")
    public ResponseEntity<String> uploadAssignment(@RequestParam Long idAss,
                                                   @RequestParam String enroll){
        UserEntity user = userService.findByEnroll(enroll);

        assignmentService.updateEntry(user,idAss);
        System.out.println("success");
        return ResponseEntity.ok("Sucess");

    }

    @GetMapping("/getAssignment")
    public ResponseEntity<List<AssignmentStudent>> getAll(){
        return ResponseEntity.ok(assignmentService.getAll().stream().map(it->
                    new AssignmentStudent(
                            it.getId(),
                            it.getBranch().getName(),
                            String.valueOf(it.getBranch().getSemester()),
                            it.getAssignment(),
                            it.getDeadline()

                    )
            ).toList()
        );
    }

    @GetMapping("/getHolidays")
    public ResponseEntity<List<HolidayEntity>> getHolidays(){
        return ResponseEntity.ok(holidayService.getAll());
    }


}

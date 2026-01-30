package com.yash.EduSmart.controllers;

import com.yash.EduSmart.Entity.HolidayEntity;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.*;
import com.yash.EduSmart.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private TimeTableService timeTableService;

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService; // (unused here but kept)

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private HolidayService holidayService;

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping("/getTimeTableByDay")
    public ResponseEntity<List<TimeTableDTO>> getTimeTableByDays(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String semester
    ) {
        try {
            String b = safeTrim(branch);
            String semStr = safeTrim(semester);

            if (b.isEmpty() || semStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            int sem;
            try {
                sem = Integer.parseInt(semStr);
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<TimeTableEntry> entries = timeTableService.getEntryByBranchAndSemester(b, sem);
            if (entries == null || entries.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<TimeTableDTO> dtos = new ArrayList<>();
            for (TimeTableEntry e : entries) {
                if (e == null) continue;

                String day = safeTrim(e.getDay());
                String subject = safeTrim(e.getSubject());
                String timing = safeTrim(e.getTiming());
                String branchName = (e.getBranch() != null) ? safeTrim(e.getBranch().getName()) : "";

                dtos.add(new TimeTableDTO(day, subject, timing, branchName));
            }

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/getStudentsList")
    public ResponseEntity<List<StudentData>> getStudentsList(
            @RequestBody(required = false) StudentsListDTO studentsListDTO
    ) {
        try {
            if (studentsListDTO == null) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            String branch = safeTrim(studentsListDTO.getBranch());
            String semStr = safeTrim(studentsListDTO.getSemester());

            if (branch.isEmpty() || semStr.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            int sem;
            try {
                sem = Integer.parseInt(semStr);
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<UserEntity> students = userService.findStudentsByBranch(branch, sem);
            if (students == null || students.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<StudentData> result = students.stream()
                    .filter(Objects::nonNull)
                    .map(u -> new StudentData(
                            safeTrim(u.getEmail()),
                            safeTrim(u.getName())
                    ))
                    .toList();

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/getAllSubjects")
    public ResponseEntity<List<String>> getAllSubjects(
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String semester
    ) {
        try {
            String b = safeTrim(branch);
            String semStr = safeTrim(semester);

            if (b.isEmpty() || semStr.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ArrayList<>(Collections.singletonList("Some field is empty")));
            }

            int sem;
            try {
                sem = Integer.parseInt(semStr);
            } catch (NumberFormatException ex) {
                return ResponseEntity.badRequest()
                        .body(new ArrayList<>(Collections.singletonList("Invalid semester")));
            }

            List<String> subjects = timeTableService.getAllSubjects(b, sem);
            if (subjects == null) subjects = Collections.emptyList();

            return ResponseEntity.ok(subjects);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ArrayList<>(Collections.singletonList("Some error occurred")));
        }
    }

    @GetMapping("/getAttendance")
    public ResponseEntity<List<AttendanceDTO>> getAttendance(
            @RequestParam(required = false) String email
    ) {
        try {
            String em = safeTrim(email);
            if (em.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            UserEntity user = userService.findByEmail(em);
            if (user == null || user.getId() == null) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<AttendanceDTO> list = attendanceService.getAttendance(user.getId());
            if (list == null) list = Collections.emptyList();

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/markAssignment")
    public ResponseEntity<String> uploadAssignment(
            @RequestParam(required = false) Long idAss,
            @RequestParam(required = false) String enroll
    ) {
        try {
            String en = safeTrim(enroll);
            if (idAss == null || idAss <= 0 || en.isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request");
            }

            UserEntity user = userService.findByEnroll(en);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            assignmentService.updateEntry(user, idAss);
            return ResponseEntity.ok("Success");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Some error occurred");
        }
    }

    @GetMapping("/getAssignment")
    public ResponseEntity<List<AssignmentStudent>> getAll() {
        try {
            var all = assignmentService.getAll();
            if (all == null || all.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<AssignmentStudent> result = all.stream()
                    .filter(Objects::nonNull)
                    .map(it -> new AssignmentStudent(
                            it.getId(),
                            it.getBranch() != null ? safeTrim(it.getBranch().getName()) : "",
                            it.getBranch() != null ? String.valueOf(it.getBranch().getSemester()) : "",
                            safeTrim(it.getAssignment()),
                            it.getDeadline()
                    ))
                    .toList();

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/getHolidays")
    public ResponseEntity<List<HolidayEntity>> getHolidays() {
        try {
            List<HolidayEntity> list = holidayService.getAll();
            if (list == null) list = Collections.emptyList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}

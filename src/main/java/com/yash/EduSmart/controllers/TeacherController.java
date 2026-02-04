package com.yash.EduSmart.controllers;

import com.yash.EduSmart.Entity.*;
import com.yash.EduSmart.dto.*;
import com.yash.EduSmart.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
    private ChatService chatService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AssignmentService assignmentService;

//    @PostMapping("/time-table-upload")
//    public ResponseEntity<String> timeTableEntry(@RequestBody(required = false) TimeTableDTO timeTableDTO) {
//        try {
//            if (timeTableDTO == null) return ResponseEntity.badRequest().body("Request body missing");
//
//            // Basic field checks (adjust according to your DTO fields)
//            String day = safeTrim(timeTableDTO.getDay());
//            String subject = safeTrim(timeTableDTO.getSubject());
//            String timing = safeTrim(timeTableDTO.getTime());
//            String branch = safeTrim(timeTableDTO.getBranch());
//
//            if (day.isEmpty() || subject.isEmpty() || timing.isEmpty() || branch.isEmpty()) {
//                return ResponseEntity.badRequest().body("Some field is empty");
//            }
//
//            timeTableService.createEntry(timeTableDTO);
//            return ResponseEntity.ok("Entry Created");
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
//        }
//    }
//
//    @PostMapping("/branch-add")
//    public ResponseEntity<String> addBranch(@RequestBody(required = false) BranchDTO branchDTO) {
//        try {
//            if (branchDTO == null) return ResponseEntity.badRequest().body("Request body missing");
//
//            String name = safeTrim(branchDTO.getName());
//            if (name.isEmpty()) {
//                return ResponseEntity.badRequest().body("Branch name is required");
//            }
//
//            branchService.createBranch(name);
//            return ResponseEntity.ok("Branch added successfully");
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occurred in adding branch");
//        }
//    }

    @PostMapping("/uploadAttendance")
    public ResponseEntity<String> uploadAttendance(@RequestBody(required = false) AttendanceUploadDTO uploadDTO) {
        try {
            if (uploadDTO == null) return ResponseEntity.badRequest().body("Request body missing");

            String branchName = safeTrim(uploadDTO.getBranch());
            String subject = safeTrim(uploadDTO.getSubjectName());
            String time = safeTrim(uploadDTO.getTime());

            if (branchName.isEmpty() || subject.isEmpty() || time.isEmpty() || uploadDTO.getDate() == null) {
                return ResponseEntity.badRequest().body("Some field is empty");
            }

            Integer sem = uploadDTO.getSemester();
            if (sem == null || sem <= 0) {
                return ResponseEntity.badRequest().body("Invalid semester");
            }

            List<AttendanceStatus> list = uploadDTO.getAttendance();
            if (list == null || list.isEmpty()) {
                return ResponseEntity.badRequest().body("Attendance list is empty");
            }

            Branch branch = branchService.getByNameAndSemester(branchName, sem);
            if (branch == null) {
                return ResponseEntity.badRequest().body("Branch not found");
            }

            TimeTableEntry timeTableEntry = timeTableService.getAttendanceUploadSlot(
                    uploadDTO.getDate().getDayOfWeek().name().toUpperCase(),
                    subject,
                    branch,
                    time
            );

            if (timeTableEntry == null) {
                return ResponseEntity.badRequest().body("No timetable slot found for given details");
            }

            int success = 0, fail = 0;


            List<String> emails = uploadDTO.getAttendance().stream()
                    .map(AttendanceStatus::getEmail)
                    .filter(Objects::nonNull)
                    .map(String::trim)
                    .filter(e -> !e.isEmpty())
                    .distinct()
                    .toList();

            List<UserEntity> users = userService.findByEmails(emails);
            Map<String, UserEntity> userMap = users.stream()
                    .collect(Collectors.toMap(UserEntity::getEmail, u -> u));

            List<Attendance> existing = attendanceService.getAllEntry(
                    timeTableEntry,
                    uploadDTO.getDate(),
                    emails
            );

            Map<String, Attendance> attendanceMap = existing.stream()
                    .collect(Collectors.toMap(
                            a -> a.getStudent().getEmail(),
                            a -> a
                    ));


            List<Attendance> toSave = new ArrayList<>();
            for (AttendanceStatus attendance : uploadDTO.getAttendance()) {
                try {
                    if (attendance == null) {
                        fail++;
                        continue;
                    }

                    String email = safeTrim(attendance.getEmail());
                    if (email.isEmpty()) {
                        fail++;
                        continue;
                    }

                    UserEntity user = userMap.get(email);
                    if (user == null) {
                        // 🔴 user not found in DB
                        fail++;
                        continue;
                    }

                    Attendance entity = attendanceMap.get(email);

                    if (entity != null) {
                        entity.setStatus(attendance.getStatus());
                        // entity.setUpdatedAt(...) if you have it
                        toSave.add(entity);
                    } else {
                        Attendance newEntry = new Attendance();
                        newEntry.setStudent(user);
                        newEntry.setTimeTable(timeTableEntry);
                        newEntry.setDate(uploadDTO.getDate());
                        newEntry.setStatus(attendance.getStatus());
                        toSave.add(newEntry);
                    }

                    success++;

                } catch (Exception ex) {
                    fail++;
                }
            }

            if (!toSave.isEmpty()) attendanceService.saveAll(toSave);



            if (success == 0) {
                return ResponseEntity.badRequest().body("Attendance upload failed. No entries were created.");
            } else if (fail > 0) {
                return ResponseEntity.ok("⚠Attendance partially uploaded. Success: " + success + ", Failed: " + fail);
            } else {
                return ResponseEntity.ok("Attendance uploaded successfully");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Some error occurred");
        }
    }

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

            List<TimeTableDTO> entries = timeTableService.getEntryByBranchAndSemester(b, sem);
            if (entries == null || entries.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<TimeTableDTO> dtos = new ArrayList<>();
            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PostMapping("/getStudentsListTeacher")
    public ResponseEntity<List<StudentData>> getStudentsList(@RequestBody(required = false) StudentsListDTO studentsListDTO) {
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

            List<UserEntity> studentsData = userService.findStudentsByBranch(branch, sem);
            if (studentsData == null || studentsData.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<StudentData> studentEmails = studentsData.stream()
                    .filter(Objects::nonNull)
                    .map(it -> new StudentData(safeTrim(it.getEmail()), safeTrim(it.getName())))
                    .toList();

            return ResponseEntity.ok(studentEmails);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/getAllBranch")
    public ResponseEntity<List<String>> getBranchList() {
        try {
            List<String> list = branchService.getAllBranch();
            if (list == null) list = Collections.emptyList();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/getAllAssignmentsByBranchAndSem")
    public ResponseEntity<List<AssignmentGetDTO>> getAll(
            @RequestParam String branch,
            @RequestParam String sem
    ) {
        try {
            int semester = Integer.parseInt(safeTrim(sem));
            List<AssignmentGetDTO> dtos = assignmentService.findByBranchAndSem(branch,semester);
            if (dtos == null || dtos.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }


            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @PutMapping("/deleteAssignment")
    public ResponseEntity<String> deleteAssignment(@RequestParam Long id){
        assignmentService.deleteEntry(id);
        return ResponseEntity.ok("DELETED");
    }

    @GetMapping("/getMyTImeTable")
    public ResponseEntity<List<TimeTableDTO>> getTimeTable(@RequestParam(required = false) String email) {
        try {
            String em = safeTrim(email);
            if (em.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            UserEntity user = userService.findByEmail(em);
            if (user == null || user.getId() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            List<TimeTableEntry> entries = timeTableService.findByTeacherId(user.getId());
            if (entries == null || entries.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            List<TimeTableDTO> timetable = entries.stream()
                    .filter(Objects::nonNull)
                    .map(it -> new TimeTableDTO(
                            safeTrim(it.getDay()),
                            safeTrim(it.getSubject()),
                            safeTrim(it.getTiming()),
                            (it.getBranch() != null)
                                    ? (safeTrim(it.getBranch().getName()) + " " + it.getBranch().getSemester())
                                    : ""
                    ))
                    .toList();

            return ResponseEntity.ok(timetable);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/getMessagesByBranchAndSem")
    public ResponseEntity<List<ChatEntity>> getGroupMessages(
            @RequestParam String branch,
            @RequestParam String sem
    ){
        try {
            List<ChatEntity> list = chatService.getMessageByReceiver(branch+" "+sem);
            return ResponseEntity.ok(list);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/getPvtMsg")
    public ResponseEntity<List<ChatEntity>> getPvtMessages(
            @RequestParam String email,
            @RequestParam String receiverEmail
    ){
        try {
            String a = safeTrim(email);
            String b = safeTrim(receiverEmail);
            if (a.isEmpty() || b.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<ChatEntity> list = chatService.getConversation(a, b);
            return ResponseEntity.ok(list);

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}

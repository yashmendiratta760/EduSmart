package com.yash.EduSmart.controllers;

import com.yash.EduSmart.Entity.ChatEntity;
import com.yash.EduSmart.Entity.HolidayEntity;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.config.SupabaseStorageClient;
import com.yash.EduSmart.dto.*;
import com.yash.EduSmart.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;

@Slf4j
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

    @Autowired
    private ChatService chatService;

    @Value("${SUPABASE_URL}")
    private String baseUrl;

    private final SupabaseStorageClient storageClient;

    public StudentController(SupabaseStorageClient storageClient) {
        this.storageClient = storageClient;
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

            List<TimeTableDTO> dtos = timeTableService.getEntryByBranchAndSemester(b, sem);
            if (dtos == null || dtos.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
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



            List<StudentData> result = userService.findStudentsByBranch(branch,sem);

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

    @GetMapping("/getAllAssignmentsByBranchAndSem")
    public ResponseEntity<List<AssignmentStudent>> getAll(
            @RequestParam String branch,
            @RequestParam String sem
    ) {
        try {
            int semester = Integer.parseInt(safeTrim(sem));
            List<AssignmentStudent> dtos = assignmentService.findAllForStudent(branch,semester);
            if (dtos == null || dtos.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }


            return ResponseEntity.ok(dtos);

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

    @GetMapping("/getMessagesByBranchAndSem")
    public ResponseEntity<List<ChatEntity>> getGroupMessagesStudent(
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
    public ResponseEntity<List<ChatEntity>> getPvtMessagesStudent(
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }


    @PutMapping("/addPvtMsg")
    public ResponseEntity<String> sendMsg(@RequestBody ChatEntity chatEntity){
        try {
            chatService.addMessage(chatEntity);
            return ResponseEntity.ok("Sent");

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occurred");
        }
    }

    @GetMapping("/getAllTeachers")
    public ResponseEntity<List<TeacherDTO>> getAllTeacher(
            @RequestParam String branch,
            @RequestParam String sem
    ){
        try {
            List<TeacherDTO> users = timeTableService.findTeachersByBranchAndSemDto(branch,sem);
            return ResponseEntity.ok(users);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/presign-download")
    public ResponseEntity<PresignDownloadResponse> presignDownload(
            @RequestBody PresignDownloadRequest request,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String path = request.getPath();
        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        Map<String, Object> supabaseResponse =
                storageClient.createSignedDownloadUrl(path);
        String url = (String) supabaseResponse.get("signedURL");

        if (url == null) {
            throw new RuntimeException("Supabase did not return url: " + supabaseResponse);
        }
        log.error("URL {}",url);

        String fullurl = baseUrl+"/storage/v1"+url;
        log.error("URL {}",fullurl);
        return ResponseEntity.ok(new PresignDownloadResponse(fullurl));
    }


    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}

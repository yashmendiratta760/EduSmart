package com.yash.EduSmart.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yash.EduSmart.Entity.*;
import com.yash.EduSmart.config.SupabaseStorageClient;
import com.yash.EduSmart.dto.*;
import com.yash.EduSmart.service.*;
import com.yash.RateLimiter.annotation.RateLimit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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
    private ChatService chatService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private RedisService redisService;

    @Value("${SUPABASE_URL}")
    private String baseUrl;

    private final SupabaseStorageClient storageClient;

    public TeacherController(SupabaseStorageClient storageClient) {
        this.storageClient = storageClient;
    }

//    @PostMapping("/time-table-upload")
//    public ResponseEntity<String> timeTableEntry(@RequestBody(required = false) TimeTableDTO timeTableDTO) {
//        try {
//            if (timeTableDTO == null) return ResponseEntity.badRequest().body("Request body missing");
//
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

    @RateLimit(limit = 10, window = 60, type = "token-bucket")
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

            String branchKey = "branch:" + branchName + ":" + sem;
            Branch branch = redisService.get(branchKey, Branch.class);

            if (branch == null) {
                branch = branchService.getByNameAndSemester(branchName, sem);
                if (branch == null) {
                    return ResponseEntity.badRequest().body("Branch not found");
                }
                redisService.set(branchKey, branch, 86400 * 7);
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
                        fail++;
                        continue;
                    }

                    Attendance entity = attendanceMap.get(email);

                    if (entity != null) {
                        entity.setStatus(attendance.getStatus());
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

    @RateLimit(limit = 60, window = 60, type = "fixed")
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

            String timetableKey = "timetable:" + b + ":" + sem;

            List<TimeTableDTO> entries = redisService.get(
                    timetableKey,
                    new TypeReference<List<TimeTableDTO>>() {}
            );

            if (entries == null) {
                entries = timeTableService.getEntryByBranchAndSemester(b, sem);

                if (entries == null || entries.isEmpty()) {
                    return ResponseEntity.ok(Collections.emptyList());
                }

                redisService.set(timetableKey, entries, 86400 * 7);
            }

            return ResponseEntity.ok(entries);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @RateLimit(limit = 30, window = 60, type = "sliding-counter")
    @PostMapping("/getStudentsListTeacher")
    public ResponseEntity<List<StudentData>> getStudentsList(
            @RequestBody(required = false) StudentsListDTO studentsListDTO) {

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

            String studentKey = "studentlist:" + branch + sem;

            List<StudentData> studentEmails = redisService.get(
                    studentKey,
                    new TypeReference<List<StudentData>>() {}
            );

            if (studentEmails == null) {
                studentEmails = userService.findStudentsByBranch(branch, sem);
                redisService.set(studentKey, studentEmails, 86400);
            }

            return ResponseEntity.ok(studentEmails);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @RateLimit(limit = 60, window = 60, type = "fixed")
    @GetMapping("/getAllBranch")
    public ResponseEntity<List<String>> getBranchList() {
        try {
            String key = "allbranches";

            List<String> list = redisService.get(
                    key,
                    new TypeReference<List<String>>() {}
            );

            if (list == null) {
                list = branchService.getAllBranch();

                if (list != null) {
                    redisService.set(key, list, 86400);
                }
            }

            if (list == null) list = Collections.emptyList();

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @RateLimit(limit = 60, window = 60, type = "sliding-counter")
    @GetMapping("/getAllAssignmentsByBranchAndSem")
    public ResponseEntity<List<AssignmentGetDTO>> getAll(
            @RequestParam String branch,
            @RequestParam String sem
    ) {
        try {
            int semester = Integer.parseInt(safeTrim(sem));

            String key = "assignment:" + branch + sem;

            List<AssignmentGetDTO> dtos = redisService.get(
                    key,
                    new TypeReference<List<AssignmentGetDTO>>() {}
            );

            if (dtos == null) {
                dtos = assignmentService.findByBranchAndSem(branch, semester);

                if (dtos != null) {
                    redisService.set(key, dtos, 86400);
                }
            }

            if (dtos == null || dtos.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList());
            }

            return ResponseEntity.ok(dtos);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @RateLimit(limit = 10, window = 60, type = "sliding-log")
    @DeleteMapping("/deleteAssignment")
    public ResponseEntity<String> deleteAssignment(@RequestParam Long id) {

        AssignmentEntity assignmentEntity = assignmentService.findById(id);

        if (assignmentEntity == null) {
            return ResponseEntity.notFound().build();
        }

        String key = "assignment:" +
                assignmentEntity.getBranch().getName() +
                assignmentEntity.getBranch().getSemester();

        assignmentService.deleteEntry(id);

        redisService.delete(key);

        return ResponseEntity.ok("DELETED");
    }

    @RateLimit(limit = 60, window = 60, type = "fixed")
    @GetMapping("/getMyTImeTable")
    public ResponseEntity<List<TimeTableDTO>> getTimeTable(Principal principal) {
        try {
            if (principal == null || principal.getName() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.emptyList());
            }

            String em = safeTrim(principal.getName());

            if (em.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            UserEntity user = userService.findByEmail(em);

            if (user == null || user.getId() == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
            }

            String key = "teachertimetable:" + user.getId();

            List<TimeTableDTO> timetable = redisService.get(
                    key,
                    new TypeReference<List<TimeTableDTO>>() {}
            );

            if (timetable == null) {
                timetable = timeTableService.findByTeacherId(user.getId());
                redisService.set(key, timetable, 86400 * 7);
            }

            return ResponseEntity.ok(timetable);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @RateLimit(limit = 120, window = 60, type = "sliding-counter")
    @GetMapping("/getMessagesByBranchAndSem")
    public ResponseEntity<List<ChatEntity>> getGroupMessages(
            @RequestParam String branch,
            @RequestParam String sem
    ) {
        try {
            List<ChatEntity> list =
                    chatService.getMessageByReceiver(branch + " " + sem);

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @RateLimit(limit = 120, window = 60, type = "sliding-log")
    @GetMapping("/getPvtMsg")
    public ResponseEntity<List<ChatEntity>> getPvtMessages(
            @RequestParam String email,
            @RequestParam String receiverEmail
    ) {
        try {
            String a = safeTrim(email);
            String b = safeTrim(receiverEmail);

            if (a.isEmpty() || b.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.emptyList());
            }

            List<ChatEntity> list =
                    chatService.getConversation(a, b);

            return ResponseEntity.ok(list);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @RateLimit(limit = 10, window = 60, type = "token-bucket")
    @PostMapping("/presign-upload")
    public ResponseEntity<PresignUploadResponse> presignUpload(
            @RequestBody PresignUploadRequest request,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String user = principal.getName();

        String safeFileName = request.getFileName()
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        String path = user + "/" +
                UUID.randomUUID() +
                "_" +
                safeFileName;

        Map<String, Object> supabaseResponse =
                storageClient.createSignedUrl(path);

        String url = (String) supabaseResponse.get("url");

        if (url == null) {
            throw new RuntimeException(
                    "Supabase did not return url: " + supabaseResponse
            );
        }

        return ResponseEntity.ok(
                new PresignUploadResponse(path, url)
        );
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

        path = URLDecoder.decode(
                path,
                StandardCharsets.UTF_8
        );

        Map<String, Object> supabaseResponse =
                storageClient.createSignedDownloadUrl(path);

        String url = (String) supabaseResponse.get("signedURL");

        if (url == null) {
            throw new RuntimeException(
                    "Supabase did not return url: " + supabaseResponse
            );
        }

        log.error("URL {}", url);

        String fullurl = baseUrl + "/storage/v1" + url;

        log.error("URL {}", fullurl);

        return ResponseEntity.ok(
                new PresignDownloadResponse(fullurl)
        );
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
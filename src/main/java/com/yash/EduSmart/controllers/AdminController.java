package com.yash.EduSmart.controllers;

import com.yash.EduSmart.dto.admin.*;
import com.yash.EduSmart.service.BranchService;
import com.yash.EduSmart.service.TimeTableService;
import com.yash.EduSmart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController
{

    @Autowired
    private UserService userService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private TimeTableService timeTableService;


    @PostMapping("/add-user")
    public ResponseEntity<SuccessDto> addUser(
            @RequestBody AddUserDto addUserDto
    )
    {
        try {
            userService.createEntry(addUserDto);
            return ResponseEntity.ok(new SuccessDto("User created Successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(new SuccessDto("Some error Occurred"));
        }
    }

    @PostMapping("/add-branch")
    public ResponseEntity<SuccessDto> addBranch(
            @RequestBody AddBranchDto addBranchDto
    )
    {
        try {
            branchService.createBranch(addBranchDto.getBranch(),addBranchDto.getSemester());
            return ResponseEntity.ok(new SuccessDto("Branch added Successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(new SuccessDto("Some error Occurred"));
        }
    }

    @PostMapping("/add-timetable")
    public ResponseEntity<SuccessDto> addTimeTable(
            @RequestBody List<AddTimeTableDto> addTimeTableDtos
    )
    {
        try {
            timeTableService.createTimeTableEntries(addTimeTableDtos);
            return ResponseEntity.ok(new SuccessDto("Branch added Successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(new SuccessDto("Some error Occurred"));
        }
    }

    @GetMapping("/getAllBranch")
    public ResponseEntity<List<AllBranchDto>> getAllBranch(){
        try {
            List<AllBranchDto> dto = branchService.getAllBranch().stream()
                    .map(AllBranchDto::new).toList();
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAllTeachers")
    public ResponseEntity<List<AllTeachersDto>> getAllTeachers(){
        try {
            List<AllTeachersDto> dto =
                    userService.findAllTeacher().stream()
                            .map(it->
                                    new AllTeachersDto(
                                            it.getEmail(),
                                            it.getName()
                                    )).toList();
            return ResponseEntity.ok(dto);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/delete-timetable")
    public ResponseEntity<SuccessDto> deleteAll(){
        try {
            timeTableService.deleteAll();
            return ResponseEntity.ok(new SuccessDto("Successfully deleted All"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new SuccessDto("Some error Occurred"));
        }
    }

    @PutMapping("/update-user")
    public ResponseEntity<SuccessDto> updateAll(){
        try {
            userService.updateAllStudents();
            return ResponseEntity.ok(new SuccessDto("Successfully updated All"));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(new SuccessDto("Some error Occurred"));
        }

    }
}

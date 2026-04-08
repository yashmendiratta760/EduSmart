package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.StudentData;
import com.yash.EduSmart.dto.UserDTO;
import com.yash.EduSmart.dto.admin.AddUserDto;
import com.yash.EduSmart.repository.AttendanceRepo;
import com.yash.EduSmart.repository.BranchRepo;
import com.yash.EduSmart.repository.TimeTableRepo;
import com.yash.EduSmart.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BranchRepo branchRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AttendanceRepo attendanceRepo;

    @Autowired
    private TimeTableRepo timeTableRepo;

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public UserEntity findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public List<UserEntity> findByEmails(List<String> email) {
        return userRepo.findByEmailIn(email);
    }

    public UserEntity findByEnroll(String enroll){
        return userRepo.findByEnroll(enroll);
    }

    public void createEntry(AddUserDto userDTO) {
        try {

            Branch branch = branchRepo.findByNameAndSemester(userDTO.getBranch(),Integer.parseInt(userDTO.getSemester()));
            if(!userRepo.existsByEmail(userDTO.getEmail())) {
                UserEntity user = new UserEntity();
                user.setEmail(userDTO.getEmail());
                user.setPassword(passwordEncoder.encode(userDTO.getPass()));
                user.setUserType(userDTO.getUser_type());
                user.setBranch(branch);
                user.setEnroll(userDTO.getEnroll());
                user.setName(userDTO.getName());
                userRepo.save(user);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<StudentData> findStudentsByBranch(String branch, int semester) {
        return userRepo.findStudentDataByBranchAndSemester(branch, semester);
    }

    public List<UserEntity> findAllTeacher(){
        return userRepo.findByUserType("TEACHER");


    }
    public void updateAllStudents(){
        attendanceRepo.truncateAttendance();
        timeTableRepo.deleteAll();
        userRepo.deleteFinalYearStudents();
        userRepo.promoteStudentsToNextSemester();
    }

}

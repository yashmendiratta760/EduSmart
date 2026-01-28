package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.UserDTO;
import com.yash.EduSmart.repository.BranchRepo;
import com.yash.EduSmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BranchRepo branchRepo;

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public UserEntity findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public UserEntity findByEnroll(String enroll){
        return userRepo.findByEnroll(enroll);
    }

    public boolean createEntry(UserDTO userDTO) {
        try {
            UserEntity user = new UserEntity();
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            user.setUserType(userDTO.getUserType());
            userRepo.save(user);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserEntity> findStudentsByBranch(String branch, int semester) {
        Branch branchOfUser = branchRepo.findByNameAndSemester(branch, semester);

        if (branchOfUser != null) {
            List<UserEntity> studentList = userRepo.findByBranch(branchOfUser);
            return studentList;
        } else return new ArrayList<>();
    }
}

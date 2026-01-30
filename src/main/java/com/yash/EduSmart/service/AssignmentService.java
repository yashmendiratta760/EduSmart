package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.AssignmentEntity;
import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.AssignmentDTO;
import com.yash.EduSmart.repository.AssignmentRepo;
import com.yash.EduSmart.repository.BranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepo assignmentRepo;

    @Autowired
    private BranchRepo branchRepo;

    public Long createEntry(AssignmentDTO assignmentDTO){
        Branch branch = branchRepo.findByNameAndSemester(assignmentDTO.getBranch(),Integer.parseInt(assignmentDTO.getSemester()));
        AssignmentEntity assignment = new AssignmentEntity();
        assignment.setAssignment(assignmentDTO.getAssignment());
        assignment.setDeadline(assignmentDTO.getDeadline());
        assignment.setBranch(branch);

        AssignmentEntity s = assignmentRepo.save(assignment);
        return s.getId();

    }

    public void deleteEntry(Long id){
        assignmentRepo.deleteById(id);
    }

    public void updateEntry(UserEntity userEntity,Long id){
        AssignmentEntity assignment = assignmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found with id: " + id));

        if (assignment.getCompletedUsers() == null) {
            assignment.setCompletedUsers(new HashSet<>());
        }
        assignment.getCompletedUsers().add(userEntity);
        assignmentRepo.save(assignment);


    }

    public List<AssignmentEntity> getAll(){
        return assignmentRepo.findAll();
    }

}

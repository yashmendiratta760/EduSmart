package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.repository.BranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {
    @Autowired
    private BranchRepo branchRepo;

    public Branch getByName(String branch) {
        if (branchRepo.existsByName(branch)) {
            return branchRepo.findByName(branch);
        }
        return null;
    }

    public void createBranch(String name,String sem) {
        Branch branch = new Branch();
        branch.setName(name);
        branch.setSemester(Integer.parseInt(sem));
        branchRepo.save(branch);
    }

    public Branch getByNameAndSemester(String name, int sem) {
        return branchRepo.findByNameAndSemester(name, sem);
    }

    public List<String> getAllBranch(){
        return branchRepo.findAll().stream().map(
                branch -> branch.getName()
        )
                .distinct().toList();
    }

}

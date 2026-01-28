package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepo extends JpaRepository<Branch, Long> {
    Branch findByName(String name);

    boolean existsByName(String name);

    Branch findByNameAndSemester(String name, int semester);
}


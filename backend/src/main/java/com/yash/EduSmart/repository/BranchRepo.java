package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BranchRepo extends JpaRepository<Branch, Long> {
    Branch findByName(String name);

    boolean existsByName(String name);

    Branch findByNameAndSemester(String name, int semester);

    @Query("SELECT DISTINCT b.name FROM Branch b")
    List<String> findAllDistinctNames();
}


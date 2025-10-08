package com.yash.ResourceManagement.repository;

import com.yash.ResourceManagement.Entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchRepo extends JpaRepository<Branch,Long>
{
    Branch findByName(String name);
    boolean existsByName(String name);
}


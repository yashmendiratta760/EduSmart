package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> findByBranch(Branch branch);

    UserEntity findByEnroll(String enroll);


}

package com.yash.ResourceManagement.repository;

import com.yash.ResourceManagement.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);
}

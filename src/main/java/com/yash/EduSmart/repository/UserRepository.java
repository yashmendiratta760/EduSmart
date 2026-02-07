package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.dto.StudentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);

    boolean existsByEmail(String email);

    List<UserEntity> findByBranch(Branch branch);

    @Query("""
    select new com.yash.EduSmart.dto.StudentData(u.email, u.name)
    from UserEntity u
    where u.userType = 'STUDENT'
      and u.branch.name = :branch
      and u.branch.semester = :semester
    """)
    List<StudentData> findStudentDataByBranchAndSemester(String branch, int semester);


    UserEntity findByEnroll(String enroll);

    List<UserEntity> findByEmailIn(List<String> emails);


}

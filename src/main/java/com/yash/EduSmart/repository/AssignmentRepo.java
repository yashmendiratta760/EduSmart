package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.AssignmentEntity;
import com.yash.EduSmart.dto.AssignmentStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AssignmentRepo extends JpaRepository<AssignmentEntity,Long>
{
    @Query("""
    select new com.yash.EduSmart.dto.AssignmentStudent(
        a.id,
        b.name,
        concat('', b.semester),
        a.assignment,
        a.deadline
    )
    from AssignmentEntity a
    join a.branch b
""")
    List<AssignmentStudent> findAllForStudent();
}

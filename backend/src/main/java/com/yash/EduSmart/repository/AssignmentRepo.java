package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.AssignmentEntity;
import com.yash.EduSmart.dto.AssignmentGetDTO;
import com.yash.EduSmart.dto.AssignmentStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

public interface AssignmentRepo extends JpaRepository<AssignmentEntity,Long>
{
    @Query("""
select new com.yash.EduSmart.dto.AssignmentStudent(
    a.id,
    b.name,
    concat('', b.semester),
    a.assignment,
    a.deadline,
    a.pathOfFile
)
from AssignmentEntity a
join a.branch b
where b.name = :branch
  and b.semester = :sem
""")
    List<AssignmentStudent> findAllForStudent(
            @Param("branch") String branch,
            @Param("sem") int sem
    );

    @Query("""
    select distinct a
    from AssignmentEntity a
    left join fetch a.completedUsers cu
    where a.branch.name = :branch
      and a.branch.semester = :sem
    """)
    List<AssignmentEntity> findAssignmentsWithCompletedUsers(
            @Param("branch") String branch,
            @Param("sem") int sem
    );


}

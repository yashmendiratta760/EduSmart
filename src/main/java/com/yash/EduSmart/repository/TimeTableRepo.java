package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.Branch;
import com.yash.EduSmart.Entity.TimeTableEntry;
import com.yash.EduSmart.dto.TeacherDTO;
import com.yash.EduSmart.dto.TimeTableDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TimeTableRepo extends JpaRepository<TimeTableEntry, Long> {
    @Query("""
    select new com.yash.EduSmart.dto.TimeTableDTO(t.day, t.subject, t.timing, b.name)
    from TimeTableEntry t
    join t.branch b
    where b.name = :branch and b.semester = :semester
    """)
    List<TimeTableDTO> findByBranchAndSemester(
            @Param("branch") String branch,
            @Param("semester") int semester
    );


    TimeTableEntry findByDayAndSubjectAndBranchAndTiming(String day, String subject, Branch branch, String time);

    @Query("""
select new com.yash.EduSmart.dto.TimeTableDTO(
  t.day, t.subject, t.timing,
  concat(b.name, ' ', b.semester)
)
from TimeTableEntry t
join t.branch b
join t.teacher te
where te.id = :teacherId
""")
    List<TimeTableDTO> getTeacherTimeTable(@Param("teacherId") Long teacherId);


    List<TimeTableEntry> findByBranch(Branch branch);

    @Query("""
    select distinct new com.yash.EduSmart.dto.TeacherDTO(u.name, u.email)
    from TimeTableEntry t
    join t.teacher u
    where t.branch.name = :branch and t.branch.semester = :sem
    """)
    List<TeacherDTO> findTeacherDtos(@Param("branch") String branch,
                                     @Param("sem") int sem);

    @Query("select distinct t.subject from TimeTableEntry t where t.branch.name=:branch and t.branch.semester=:sem")
    List<String> findDistinctSubjects(String branch, int sem);



}

package com.yash.EduSmart.repository;

import com.yash.EduSmart.Entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepo extends JpaRepository<ChatEntity,Long> {
    List<ChatEntity> findBySenderAndReceiver(String email,String receiver);
    List<ChatEntity> findByReceiver(String receiver);
    @Query("""
    SELECT c FROM ChatEntity c
    WHERE (c.sender = :a AND c.receiver = :b)
       OR (c.sender = :b AND c.receiver = :a)
    ORDER BY c.timeStamp ASC
    """)
    List<ChatEntity> findConversation(@Param("a") String a, @Param("b") String b);

}

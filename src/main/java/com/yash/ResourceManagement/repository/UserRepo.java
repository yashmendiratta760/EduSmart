package com.yash.ResourceManagement.repository;

import com.yash.ResourceManagement.Entity.UserEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<UserEntity, ObjectId>
{
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
}

package com.yash.ResourceManagement.service;

import com.yash.ResourceManagement.Entity.UserEntity;
import com.yash.ResourceManagement.dto.UserDTO;
import com.yash.ResourceManagement.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    @Autowired
    private UserRepo userRepo;

    public boolean existsByEmail(String email)
    {
        return userRepo.existsByEmail(email);
    }

    public UserEntity findByEmail(String email)
    {
        return userRepo.findByEmail(email);
    }

    public boolean createEntry(UserDTO userDTO)
    {
        try {
            UserEntity user = new UserEntity();
            user.setEmail(userDTO.getEmail());
            user.setPassword(userDTO.getPassword());
            userRepo.save(user);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

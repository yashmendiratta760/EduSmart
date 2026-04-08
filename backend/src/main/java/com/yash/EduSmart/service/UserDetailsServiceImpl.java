package com.yash.EduSmart.service;

import com.yash.EduSmart.Entity.UserEntity;
import com.yash.EduSmart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null) {
            throw new UsernameNotFoundException("username not found");
        }
        return User.builder().username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities("ROLE_"+userEntity.getUserType())
                .build();
    }
}

package com.yash.ResourceManagement.service;

import com.yash.ResourceManagement.Entity.UserEntity;
import com.yash.ResourceManagement.repository.UserRepository;
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

        if(userEntity==null)
        {
            throw new UsernameNotFoundException("username not found");
        }
        return User.builder().username(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities(userEntity.getUserType())
                .build();
    }
}

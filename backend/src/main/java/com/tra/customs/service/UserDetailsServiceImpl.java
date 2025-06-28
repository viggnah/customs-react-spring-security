package com.tra.customs.service;

import com.tra.customs.entity.User;
import com.tra.customs.repository.UserRepository;
import com.tra.customs.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameWithRolesAndAuthorities(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));
        
        return UserPrincipal.create(user);
    }
}
